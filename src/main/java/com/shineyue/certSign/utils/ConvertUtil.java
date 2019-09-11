package com.shineyue.certSign.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @PackageName: com.shineyue.certSign.utils
 * @Description: TODO
 * @author: 罗绂威
 * @date: wrote on 2019/8/26
 */
public class ConvertUtil {
    public static InputStream StringToInputStreamFun(String str) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    // 图片转化成base64字符串 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
    public static String GetImageBaes64Str(String imgFilePath) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    // 图片转化成base64字符串 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
    public static String GetImageBaes64Str(MultipartFile file) {
        byte[] data = null;
        // 读取图片字节数组
        try {

            data = file.getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    // base64 转inputStream
    public static InputStream GetImageImput(String imgBaseStr) {
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] byteData = decoder.decodeBuffer(imgBaseStr);
            stream = new ByteArrayInputStream(byteData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }




    public static String encodeStr(String plainText) {
        byte[] b;
        String s = null;
        Base64 base64 = new Base64();
        try {
            b = plainText.getBytes("UTF-8");
            b = base64.encode(b);
            s = new String(b, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String decodeStr(String encodeStr) {
        byte[] b;
        String s = null;
        Base64 base64 = new Base64();
        try {
            b = encodeStr.getBytes("UTF-8");
            b = base64.decode(b);
            s = new String(b, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }


    public static String getPDFBinary(File file) {
        FileInputStream fin = null;
        BufferedInputStream bin = null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout = null;
        try {
            fin = new FileInputStream(file);
            bin = new BufferedInputStream(fin);
            baos = new ByteArrayOutputStream();
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while (len != -1) {
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }
            bout.flush();
            byte[] bytes = baos.toByteArray();

            return Base64.encodeBase64String(bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
                bin.close();
                baos.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void inputStreamToFile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File multipartFileToFile(MultipartFile file) {
        File f = null;
        try {
            InputStream is = file.getInputStream();
            f = new File(file.getOriginalFilename());

            OutputStream os = new FileOutputStream(f);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public static String companyPicPoint(int pageNum,int picx,int picy,int poix,int poiy){
        StringBuffer picSizesStr = new StringBuffer();
        StringBuffer picPointsStr = new StringBuffer();
        StringBuffer str = new StringBuffer();
        picSizesStr.append("(").append(picx).append(",").append(picy).append(")");
        picPointsStr.append("(").append(poix).append(",").append(poiy).append(")");
        str.append("'PageNums':'").append(pageNum).append("','picSizes':'").append(picSizesStr).append("','picPoints':'").append(picPointsStr).append("'");
        return str.toString();
    }

    public static void base64StringToFile(String base64String,String filePath) {
        InputStream bin = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            byte[] bytes = Base64.decodeBase64(base64String);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            bin = new BufferedInputStream(bais);
            File file = new File(filePath);
            fout = new FileOutputStream(file);
            bout = new BufferedOutputStream(fout);
            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while (len != -1) {
                bout.write(buffers, 0, len);
                len = bin.read(buffers);
            }
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bin.close();
                fout.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
