package lhg.common.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

//    /**
//     * 压缩文件和文件夹
//     *
//     * @throws Exception
//     */
//    public static void zipFiles(String zipFile, String ...srcFiles) throws Exception {
//        //创建ZIP
//        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFile));
//        //创建文件
//        for (String srcFile : srcFiles) {
//            File file = new File(srcFile);
//            //压缩
//            zipFile(file.getParent() + File.separator, file.getName(), outZip);
//        }
//        //完成和关闭
//        outZip.finish();
//        outZip.close();
//    }
//
//    /**
//     * 压缩文件
//     *
//     * @param folderString
//     * @param fileString
//     * @param zipOutputSteam
//     * @throws Exception
//     */
//    private static void zipFile(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
//        Log.i("folderString", folderString + "n" +
//                "fileString:" + fileString + "n==========================");
//        if (zipOutputSteam == null)
//            return;
//        File file = new File(folderString + fileString);
//        if (file.isFile()) {
//            ZipEntry zipEntry = new ZipEntry(fileString);
//            FileInputStream inputStream = new FileInputStream(file);
//            zipOutputSteam.putNextEntry(zipEntry);
//            int len;
//            byte[] buffer = new byte[4096];
//            while ((len = inputStream.read(buffer)) != -1) {
//                zipOutputSteam.write(buffer, 0, len);
//            }
//            zipOutputSteam.closeEntry();
//        } else {
//            //文件夹
//            String fileList[] = file.list();
//            //没有子文件和压缩
//            if (fileList.length <= 0) {
//                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
//                zipOutputSteam.putNextEntry(zipEntry);
//                zipOutputSteam.closeEntry();
//            }
//            //子文件和递归
//            for (int i = 0; i < fileList.length; i++) {
//                zipFile(folderString+fileString+"/", fileList[i], zipOutputSteam);
//            }
//        }
//    }

    public static void zip(String src, String dest) {
        ZipOutputStream out = null;

        try {
            File outFile = new File(dest);
            File fileOrDirectory = new File(src);
            out = new ZipOutputStream(new FileOutputStream(outFile));
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                File[] entries = fileOrDirectory.listFiles();

                for(int i = 0; i < entries.length; ++i) {
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

    }

    public static void zipFiles(List<String> srcs, String dest) {
        File outFile = new File(dest);
        ZipOutputStream outZip = null;

        try {
            outZip = new ZipOutputStream(new FileOutputStream(outFile));
            Iterator var5 = srcs.iterator();

            while(var5.hasNext()) {
                String src = (String)var5.next();
                File file = new File(src);
                zipFileOrDirectory(outZip, file, "");
            }
        } catch (FileNotFoundException var19) {
            var19.printStackTrace();
        } finally {
            if (outZip != null) {
                try {
                    outZip.close();
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }

        }

    }

    private static void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) {
        FileInputStream in = null;

        try {
            int i;
            if (!fileOrDirectory.isDirectory()) {
                byte[] buffer = new byte[4096];
                in = new FileInputStream(fileOrDirectory);
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                out.putNextEntry(entry);

                while((i = in.read(buffer)) != -1) {
                    out.write(buffer, 0, i);
                }

                out.closeEntry();
            } else if (!Arrays.asList(".", "..", "../").contains(fileOrDirectory.getName())){
                File[] entries = fileOrDirectory.listFiles();
                for(i = 0; i < entries.length; ++i) {
                    zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

    }

    public static List<String> unzip(String zipFileName, String outDir) {
        ZipFile zipFile = null;
        ArrayList filesInZip = new ArrayList();

        try {
            zipFile = new ZipFile(zipFileName);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = null;
            File dest = new File(outDir);
            dest.mkdirs();

            while(e.hasMoreElements()) {
                zipEntry = (ZipEntry)e.nextElement();
                String entryName = zipEntry.getName();
                InputStream in = null;
                FileOutputStream out = null;
                if (Arrays.asList(".", "..", "../").contains(entryName)) {
                    continue;
                }

                try {
                    File f;
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        f = new File(outDir + File.separator + name);
                        f.mkdirs();
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            f = new File(outDir + File.separator + entryName.substring(0, index));
                            f.mkdirs();
                        }

                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            f = new File(outDir + File.separator + entryName.substring(0, index));
                            f.mkdirs();
                        }

                        f = new File(outDir + File.separator + zipEntry.getName());
                        filesInZip.add(f.getPath());
                        in = zipFile.getInputStream(zipEntry);
                        out = new FileOutputStream(f);
                        byte[] by = new byte[1024];

                        int c;
                        while((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }

                        out.flush();
                    }
                } catch (IOException var41) {
                    var41.printStackTrace();
                    throw new IOException("解压失败：" + var41.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException var40) {
                        }
                    }

                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException var39) {
                        }
                    }

                }
            }
        } catch (IOException var43) {
            var43.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException var38) {
                }
            }

        }

        return filesInZip;
    }

    public static void removeZipFiles(List<String> pkgZipNames) {
        File zipFile = null;
        if (pkgZipNames != null) {
            Iterator var3 = pkgZipNames.iterator();

            while(var3.hasNext()) {
                String pkgZipName = (String)var3.next();
                zipFile = new File(pkgZipName);
                if (zipFile.exists()) {
                    zipFile.delete();
                }
            }
        }

    }

}
