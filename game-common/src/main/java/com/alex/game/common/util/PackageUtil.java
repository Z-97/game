package com.alex.game.common.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
public class PackageUtil {

	/**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    public static List<Class<?>> getClassName(String packageName) throws IOException {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName  包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    public static List<Class<?>> getClassName(String packageName, boolean childPackage) throws IOException {
        List<Class<?>> fileNames = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> urls = loader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null)
                continue;
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames.addAll(getClassNameByFile(url.getPath(), childPackage,loader));
            } else if (type.equals("jar")) {
                fileNames.addAll(getClassNameByJar(url.getPath(), childPackage,loader));
            }
        }
        
        return fileNames;
    }
    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath     文件路径
     *                     类名集合
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    private static List<Class<?>> getClassNameByFile(String filePath, boolean childPackage,ClassLoader loader) throws UnsupportedEncodingException {
        List<Class<?>> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        if (childFiles == null)
            return myClassName;
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage,loader));
                }
            } else {
                String childFilePath = childFile.getPath();
                childFilePath = clearPath(childFilePath);
                if (childFilePath.endsWith(".class")) {
                	
                	childFilePath = childFilePath.replace("/", ".");
                    childFilePath = childFilePath.substring(0,childFilePath.length()-6).replace("/", ".");
                    if((childFilePath.contains("Req")||childFilePath.contains("Res"))&&childFilePath.contains("Handler")) {
                    	Class<?> myclass;
    					try {
    						myclass = loader.loadClass(childFilePath);
    						myClassName.add(myclass);
    					} catch (ClassNotFoundException e) {
    						
    					}
                    }
                    
                    
                   
                }
            }
        }
        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath      jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws UnsupportedEncodingException
     */
    private static List<Class<?>> getClassNameByJar(String jarPath, boolean childPackage,ClassLoader loader) throws UnsupportedEncodingException {
        List<Class<?>> myClassName = new ArrayList<Class<?>>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
       // jarFilePath = UrlDecode.getURLDecode(jarFilePath);
        String packagePath = jarInfo[1].substring(1);
        JarFile jarFile;
        try {
        	jarFile= new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            if((entryName.contains("Req")||entryName.contains("Res"))&&entryName.contains("Handler")) {
                            	  Class<?> myclass = loader.loadClass(entryName);
                                  myClassName.add(myclass);
                            }
                          
                        }
                    } else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            if((entryName.contains("Req")||entryName.contains("Res"))&&entryName.contains("Handler")) {
                            	 Class<?> myclass = loader.loadClass(entryName);
                                 myClassName.add(myclass);
                            }
                           
                        }
                    }
                }
            }
        } catch (Exception e) {
            
        }
        return myClassName;
    }

	public static List<Class<?>> getAllAssignedClass(Class<?> handler,String pk) throws IOException, ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.addAll(getClassName(pk));
		return classes; 
		
	}
	  private static String clearPath(String childFilePath) {
			return childFilePath.substring(childFilePath.indexOf("com"),childFilePath.length());
		}
}
