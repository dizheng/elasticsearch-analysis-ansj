package org.ansj.elasticsearch.pubsub.redis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.ansj.elasticsearch.index.config.AnsjElasticConfigurator;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

public class FileUtils {

	public static ESLogger logger = Loggers.getLogger("ansj-redis-msg-file");

	  public static void remove(String content) {
	    try {
	      removeFile(content, AnsjElasticConfigurator.REDIS_LIB_FILE, false);
	    } catch (Exception e) {
	      logger.error("remove exception", e);
	      e.printStackTrace();
	    }
	  }

	  public static void append(String content)
	  {
	    try {
	      appendFile(content, AnsjElasticConfigurator.REDIS_LIB_FILE);
	    } catch (Exception e) {
	      logger.error("append exception", e);
	      e.printStackTrace();
	    }
	  }

	  public static void removeAMB(String content) {
	    try {
	      File file = new File(AnsjElasticConfigurator.DEFAULT_AMB_FILE_LIB_PATH);
	      removeFile(content, file, true);
	    } catch (Exception e) {
	      logger.error("removaAMB exception", e);
	      e.printStackTrace();
	    }
	  }

	  public static void appendAMB(String content)
	  {
	    try {
	      File file = new File(AnsjElasticConfigurator.DEFAULT_AMB_FILE_LIB_PATH);
	      appendFile(content, file);
	    } catch (Exception e) {
	      logger.error("appendAMB exception", e);
	      e.printStackTrace();
	    }
	  }

	  private static void appendFile(final String content, final File file) throws Exception
	  {
          final SecurityManager sm = System.getSecurityManager();
          if (sm != null) {
              sm.checkPermission(new SpecialPermission());
          }
          AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              @Override
              public Object run() throws IOException {
                  BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                  writer.write(content);
                  writer.newLine();
                  writer.close();
                  return null;
              }
          });
      }

	  private static void removeFile(final String content, final File file, final boolean head) throws Exception
	  {
          final SecurityManager sm = System.getSecurityManager();
          if (sm != null) {
              sm.checkPermission(new SpecialPermission());
          }
          AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              @Override
              public Object run() throws IOException {
                  BufferedReader reader = new BufferedReader(new FileReader(file));
                  List<String> list = new ArrayList<>();
                  String text = reader.readLine();
                  while (text != null) {
                      if (match(content, text, head)) {
                          list.add(text);
                      }
                      text = reader.readLine();
                  }
                  reader.close();
                  BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                  for (String item : list) {
                      writer.write(item);
                      writer.newLine();
                  }
                  writer.close();
                  return null;
              }
          });
      }

	  private static boolean match(String content, String text, boolean head) {
	    if (head)
	      return !text.trim().matches("^" + content + "\\D*$");
	    return !text.trim().equals(content);
	  }

	  public static void main(String[] args) {
	    Pattern p = Pattern.compile("^满意\\D*$");
	    System.out.println(p.matcher("满意  满      a       意      a").matches());
	    System.out.println(p.matcher("满哈-满,意").matches());
	    System.out.println("满哈-满,意".replace(",", "\t"));
	  }
	
	
	
}
