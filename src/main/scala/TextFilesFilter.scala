import java.io.{File, FileFilter}

/**
  * Created by junyoung on 2017. 8. 14..
  */
class TextFilesFilter extends FileFilter{
  // Find only text file
  override def accept(pathname: File): Boolean = {
    pathname.getName.toLowerCase().endsWith(".txt")
  }
}
