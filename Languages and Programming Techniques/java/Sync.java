import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Sync {
    private static final ArrayList<String> toDelete=new ArrayList<>(),toCopy=new ArrayList<>();
    public static void main(String[] args){
        String s=args[0],d=args[1];
        File f1=new File(s);
        File f2=new File(d);
        sync(f1,f2);
        if (toDelete.isEmpty() && toCopy.isEmpty()) System.out.println("IDENTICAL");
        else {
            Collections.sort(toDelete);
            Collections.sort(toCopy);
            toDelete.forEach(System.out::println);
            toCopy.forEach(System.out::println);
        }
    }

    private static void sync(File s,File d){
        syncD(s,d,"");
        syncS(s,d,"");
    }

    private static void syncD(File s,File d,String curDir){
        for (File f:d.listFiles()){
            File file=find(f,s.listFiles());
            if (file==null) deleteFile(f,curDir);
            else{
                if (file.isDirectory()) syncD(file,f,curDir+f.getName()+"/");
                else if (!equals(file,f)){
                    deleteFile(f,curDir);
                    copyFile(f,curDir);
                }
            }
        }
    }

    private static void syncS(File s,File d,String curDir){
        for (File f:s.listFiles()){
            File file=find(f,d.listFiles());
            if (f.isDirectory()){
                if (file==null){
                    for (File t:f.listFiles()) copyFile(t,curDir+f.getName()+"/");
                }else syncS(f,file,curDir+f.getName()+"/");
            }else if (file==null) copyFile(f,curDir);
        }
    }

    private static void deleteFile(File f,String path){
        toDelete.add("DELETE "+path+f.getName());
    }

    private static void copyFile(File f,String path){
        toCopy.add("COPY "+path+f.getName());
    }


    private static File find(File f,File[] files){
        for (File file:files){
            if (file.getName().equals(f.getName())) return file;
        }
        return null;
    }

    private static boolean equals(File s,File d){
        try(BufferedInputStream sIn=new BufferedInputStream(new FileInputStream(s));
            BufferedInputStream dIn=new BufferedInputStream(new FileInputStream(d))){
            int a,b;
            while ((a=sIn.read())!=-1 && (b=dIn.read())!=-1){
                if (a!=b) return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}

