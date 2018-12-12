//package com.iris.lucene;
//import net.contentobjects.jnotify.JNotify;
//import net.contentobjects.jnotify.JNotifyException;
//import net.contentobjects.jnotify.JNotifyListener;
//import org.apache.log4j.Logger;
//
//import java.lang.reflect.Field;
//import java.util.Arrays;
//import java.util.Properties;
//
//public class test {
//
//    static Logger log = Logger.getLogger(test.class);
//    /**
//     * jnotify动态库 - 32位
//     */
//    static final String NATIVE_LIBRARIES_32BIT = "/lib/native_libraries/32bits/";
//    /**
//     * jnotify动态库 - 64位
//     */
//    static final String NATIVE_LIBRARIES_64BIT = "/lib/native_libraries/64bits/";
//
//    public static void main(String[] args) throws JNotifyException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//
//        System.out.println("-----------Jnotify test ---------");
//
//        Properties sysProps = System.getProperties();
//        String osArch = (String) sysProps.get("os.arch");
//        String osName = (String) sysProps.get("os.name");
//        String userDir = (String) sysProps.getProperty("user.dir");
//        System.out.println("os.arch: " + osArch);
//        System.out.println("os.name: " + osName);
//        System.out.println("userDir: " + userDir);
//        System.out.println("java.class.path: " + sysProps.get("java.class.path"));
//
//        // 直接调用Jnotify时， 会发生异常：java.lang.UnsatisfiedLinkError: no jnotify_64bit in java.library.path
//        // 这是由于Jnotify使用JNI技术来加载dll文件，如果在类路径下没有发现相应的文件，就会抛出此异常。
//        // 因此可以通过指定程序的启动参数: java -Djava.library.path=/path/to/dll，
//        // 或者是通过修改JVM运行时的系统变量的方式来指定dll文件的路径，如下：
//
//        // 判断系统是32bit还是64bit，决定调用对应的dll文件
//        String jnotifyDir = NATIVE_LIBRARIES_64BIT;
//        if (!osArch.contains("64")) {
//            jnotifyDir = NATIVE_LIBRARIES_32BIT;
//        }
//        System.out.println("jnotifyDir: " + jnotifyDir);
//        // 获取目录路径
//        String pathToAdd = userDir + jnotifyDir ;
//        boolean isAdded = false;
//        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
//        usrPathsField.setAccessible(true);
//        final String[] paths = (String[]) usrPathsField.get(null);
//        System.out.println("usr_paths: " + Arrays.toString(paths));
//        for (String path : paths) {
//            if (path.equals(pathToAdd)) {
//                isAdded  = true;
//                break;
//            }
//        }
//        if (!isAdded) {
//            final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
//            newPaths[newPaths.length - 1] = pathToAdd;
//            usrPathsField.set(null, newPaths);
//        }
//
//        System.out.println("java.library.path: " + System.getProperty("java.library.path"));
//        System.out.println("usr_paths: " + Arrays.toString((String[]) usrPathsField.get(null)));
//        usrPathsField.setAccessible(false);
//        System.out.println("类路径加载完成");
//
//        // 监听F盘下的文件事件
//        JNotify.addWatch("E:\\data\\luceneInfoDir\\", JNotify.FILE_ANY, true, new JNotifyListener() {
//            @Override
//            public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
//                System.out.println("wd = " + wd + ", rootPath = " + rootPath);
//                System.out.println("oldName = " + oldName + ", newName = " + newName);
//            }
//            @Override
//            public void fileModified(int wd, String rootPath, String fileName) {
//                System.out.println("fileModified");
//            }
//            @Override
//            public void fileDeleted(int wd, String rootPath, String fileName) {
//                System.out.println("fileDeleted");
//            }
//            @Override
//            public void fileCreated(int wd, String rootPath, String fileName) {
//                System.out.println("fileDeleted");
//            }
//        });
//        while (true) {
//
//        }
//    }
//}