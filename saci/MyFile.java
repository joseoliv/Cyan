package saci;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import ast.CompilationUnit;
import ast.CompilationUnitSuper;
import ast.CyanPackage;
import ast.ExprGenericPrototypeInstantiation;
import ast.IReceiverCompileTimeMessageSend;
import ast.Prototype;
import meta.Tuple2;

public class MyFile {

    public MyFile( String filename ) {
        this.filename = filename;
    }

    public String getName() {
        return filename;
    }

    private String filename;
    public static final int
       ok_e = 0,
       do_not_exist_e = 1,
       cannot_be_read_e = 2,
       read_error_e = 3,
       write_error_e = 4,
       close_error_e = 5,
       open_error_e = 6;

    private int error;

    public int getError() {
        return error;
    }

    /**
     * delete non-directory files of <code>f</code>. This method do not delete files of sub-directories
       @param f
       @return
     */
    public static String deleteNonDirFiles(File f) {
    	if ( f.exists() ) {
    		for ( File aFile : f.listFiles() ) {
    			if (  ! aFile.isDirectory() ) {
    				if ( ! aFile.delete() )
    					return "File '" + aFile.getAbsolutePath() + "' cannot be deleted";
    			}
    		}
    	}
    	return null;
    }

    public static boolean writeFileText(String filename, char toWrite[]) {
    	MyFile myf = new MyFile(filename);
    	return myf.writeFile(toWrite);
    }

    public boolean writeFile(char toWrite[])  {
    	FileWriter outFile;
    	boolean ret = true;

    	try {
			outFile = new FileWriter(filename);
		} catch (IOException e) {
			error = open_error_e;
			return false;
		}
		try {

			int size = toWrite.length;
			int i;
			for (i = 0; i < size; ++i) {
				if ( toWrite[i] == '\0')
					break;
			}
			size = i;
			//if ( toWrite[size-1] == '\0' )
				//size--;
			outFile.write(toWrite, 0, size);
		} catch (IOException e) {
			error = write_error_e;
			ret = false;
		}
		try {
			outFile.close();
		} catch (IOException e) {
			ret = false;
			error = close_error_e;
		}
		return ret;
    }

    public static boolean write(String filenameToWrite, StringBuilder sb) {
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(filenameToWrite))) {
    		bw.write(sb.toString());
    		bw.flush();
    	} catch (IOException e) {
    		return false;
    	}
    	return true;
    }

    public List<String> readLinesFile() {
    	List<String> list = new ArrayList<String>();

    	FileReader fr;
    	String s;
    	BufferedReader br = null;
		try {
			fr = new FileReader(filename);
	    	br = new BufferedReader(fr);
	    	while( (s = br.readLine()) != null )
	    		list.add(s);

         	fr.close();
		} catch (IOException e) {
			error = read_error_e;
			if ( br != null )
				try {
				    br.close();
				}
				catch (IOException e1) {
				}
			return null;
		}
		return list;

    }

    /**
     * read file 'file'. The first tuple element is an error code. spareCharAtEnd is
     * the number of spare characters that will be allocated to the char array.
     * That is, if the file has n characters, the returned char array will have
     * n + spareCharAtEnd elements.
       @param file
       @return
     */
    static public Tuple2<Integer, char []> readFile(File file, int spareCharAtEnd) {
        FileReader stream;
        int numChRead;

        int error = ok_e;

        if ( ! file.exists() ) {
           error = do_not_exist_e;
           return new Tuple2<Integer, char []>(error, null);
        }
        else if ( ! file.canRead() ) {
           error = cannot_be_read_e;
           return new Tuple2<Integer, char []>(error, null);
         }

         try {
             stream = new FileReader(file);
         } catch ( FileNotFoundException e ) {
            error = do_not_exist_e;
            return new Tuple2<Integer, char []>(error, null);
         }
         char []input = new char[ (int ) file.length() ];

         try {
            numChRead = stream.read( input, 0, (int ) file.length() + spareCharAtEnd );
         } catch ( IOException e ) {
            error = cannot_be_read_e;
            try {
				stream.close();
			}
			catch (IOException e1) {
			}
            return new Tuple2<Integer, char []>(error, null);
         }

         if ( numChRead != file.length() ) {
            error = read_error_e;
            try {
				stream.close();
			}
			catch (IOException e1) {
			}
            return new Tuple2<Integer, char []>(error, null);
         }
         try {
            stream.close();
         } catch ( IOException e ) {
            error = close_error_e;
            return new Tuple2<Integer, char []>(error, null);
         }
         return new Tuple2<Integer, char []>(error, input);


    }

    @SuppressWarnings("resource")
	public char []readFile() {
        FileReader stream;
        int numChRead;

        error = ok_e;

        File file = new File(filename);
        if ( ! file.exists() ) {
           error = do_not_exist_e;
           return null;
        }
        else if ( ! file.canRead() ) {
           error = cannot_be_read_e;
           return null;
         }

         try {
             stream = new FileReader(file);
         } catch ( FileNotFoundException e ) {
            error = do_not_exist_e;
            return null;
         }
                // one more character for '\0' at the end that will be added by the
                // Compiler
         char []input = new char[ (int ) file.length() + 1 ];

         try {
            numChRead = stream.read( input, 0, (int ) file.length() );
         } catch ( IOException e ) {
            error = cannot_be_read_e;
            return null;
         }

         if ( numChRead != file.length() ) {
            error = read_error_e;
            return null;
         }
         try {
            stream.close();
         } catch ( IOException e ) {
            error = close_error_e;
            return null;
         }
         return input;
    }

    /**
     * read the file. If withCharZero is true, the char array returned will have an '\0' as the last character.
     * If withCharZero is false, there will not be '\0' at the end. If allocateOneMoreChar is true,
     * allocate one more char for the char array.
       @param addCharZero
       @param allocateOneMoreChar
       @param removeCharZero
       @return
     */
    @SuppressWarnings("resource")
	public char []readFile(boolean withCharZero, boolean allocateOneMoreChar) {
        FileReader stream;
        int numChRead;

        error = ok_e;

        File file = new File(filename);
        if ( ! file.exists() ) {
           error = do_not_exist_e;
           return null;
        }
        else if ( ! file.canRead() ) {
           error = cannot_be_read_e;
           return null;
         }

         try {
             stream = new FileReader(file);
         } catch ( FileNotFoundException e ) {
            error = do_not_exist_e;
            return null;
         }
                // one more character for '\0' at the end that will be added by the
                // Compiler
         int extraSizeInput = allocateOneMoreChar || withCharZero ? 1 : 0;
         char []input = new char[ (int ) file.length() + extraSizeInput ];

         try {
            numChRead = stream.read( input, 0, (int ) file.length() );
         } catch ( IOException e ) {
            error = cannot_be_read_e;
            return null;
         }

         if ( numChRead != file.length() ) {
            error = read_error_e;
            return null;
         }
         if ( withCharZero ) {
        	 if ( input[ (int ) file.length()-1] != '\0' )
        		 input[ (int ) file.length()-1] = '\0';
         }
         else {
        	 if ( input[ (int ) file.length()-1] == '\0' ) {
        		 int sizeNI = (int ) file.length() + extraSizeInput;
        		 char []ni = new char[ sizeNI];
        		 System.arraycopy(input, 0, ni, 0, sizeNI );
        		 input = ni;
        	 }
         }
         try {
            stream.close();
         } catch ( IOException e ) {
            error = close_error_e;
            return null;
         }
         return input;
    }


    public static boolean deleteFileDirectory(File file) {
		if ( file.isFile() ) {
			return file.delete();
		}
		else {
			boolean ok = true;
		    File[] contents = file.listFiles();
		    if ( contents != null ) {
		        for ( File f : contents ) {
		        	ok = ok && deleteFileDirectory(f);
		        }
		    }
		    return ok && file.delete();
		}
	}

	static public void write(CompilationUnitSuper compUnit) {
		if ( compUnit instanceof CompilationUnit ) {
			CompilationUnit c = (CompilationUnit ) compUnit;
			if ( c.getProgram() != null ) {
				// it is a Cyan program. It is not a '.pyan' project file
				String dir = c.getProgram().getProject().getProjectDir();
				if ( ! dir.endsWith(NameServer.fileSeparatorAsString) )
					dir += NameServer.fileSeparatorAsString;
				String filename = dir + "full-";
				if ( compUnit instanceof CompilationUnit && ((CompilationUnit) compUnit).getCyanPackage() != null ) {
					filename += ((CompilationUnit) compUnit).getCyanPackage().getName().replace('.', '_') + "-";
				}
				filename += compUnit.getFilename();
				MyFile.writeFileText( filename, compUnit.getText());
			}
			else {
				String dir = c.getCanonicalPathUpDir();
				if ( ! dir.endsWith(NameServer.fileSeparatorAsString) )
					dir += NameServer.fileSeparatorAsString;
				MyFile.writeFileText( dir + "pyanWithErr.txt", compUnit.getText());
			}
		}
    }


	static public void writePrototypesToFile(HashSet<CompilationUnit> compilationUnitToWriteList, String extToAdd) {
		if ( compilationUnitToWriteList != null && compilationUnitToWriteList.size() > 0 ) {
			for ( CompilationUnit compUnit : compilationUnitToWriteList ) {
				write(compUnit);
				/*
				String s = compUnit.getFullFileNamePath();
				int lastIndexOf = s.lastIndexOf(NameServer.cyanSourceFileExtension);
				if ( lastIndexOf > 0 ) {
					s = s.substring(0, lastIndexOf - 1) + extToAdd + "." + NameServer.cyanSourceFileExtension;
					MyFile.writeFileText(s, compUnit.getText());
				}
				*/
			}
		}
	}

	static public void writePrototypesToFile(HashSet<ExprGenericPrototypeInstantiation> compilationUnitToWriteList,  String extToAdd, Env env) {
		if ( compilationUnitToWriteList != null && compilationUnitToWriteList.size() > 0 ) {
			for ( ExprGenericPrototypeInstantiation exprGPI : compilationUnitToWriteList ) {
				TupleTwo<String, CompilationUnit> t = exprGPI.returnsNameWithPackage(env);
				if ( t != null && t.f2 != null ) {
					CompilationUnit compUnit = t.f2;
					write(compUnit);

				}
				else
					env.error(null, "There is a metaobject message send to '" + exprGPI.getName() + "'. However, I cannot find this prototype");
			}
		}
	}


	static public void writePrototypesToFile(HashSet<IReceiverCompileTimeMessageSend> compilationUnitToWriteList,  Env env) {

		// List<String> errorMessageList = new ArrayList<String>();

		if ( compilationUnitToWriteList != null && compilationUnitToWriteList.size() > 0 ) {
			for ( IReceiverCompileTimeMessageSend exprGPI : compilationUnitToWriteList ) {

				try {

					String prototypeName = exprGPI.getPrototypeName();
					String packageName = exprGPI.getPackageName();
					Prototype pu = null;
					if ( packageName == null ) {
						for ( CyanPackage cp : env.getProject().getPackageList() ) {
							Prototype pu2 = cp.searchPublicNonGenericPrototype(prototypeName);
							if ( pu2 != null ) {
								pu = pu2;
								break;
							}
						}
					}
					else {
						pu = env.searchPackagePrototype(packageName, prototypeName);
					}

					if ( pu != null ) {
						write(pu.getCompilationUnit());
					}
					else {
						env.error(null,  "There is a compile-time message send to '" + exprGPI.getName() + "'. However, I cannot find this prototype", false, true);
					}

				}
				catch (Throwable e ) {
					env.error(null, "At least one compilation unit (.cyan file) cannot be written as 'full-protoName.cyan' in the directory of the project " +
				           "because the compile error in it prevented the compiler "
							+ " from getting the information that is needed to write the file");
				}
			}
		}
	}

	static String readFile(String path, Charset encoding) {
		try {
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
		}
		catch (IOException e ) {
			return null;
		}
	}

	public class CopyFile {

		   public void main(String args[]) throws IOException {
		      FileInputStream in = null;
		      FileOutputStream out = null;

		      try {
		         in = new FileInputStream("input.txt");
		         out = new FileOutputStream("output.txt");

		         int c;
		         while ((c = in.read()) != -1) {
		            out.write(c);
		         }
		      }finally {
		         if (in != null) {
		            in.close();
		         }
		         if (out != null) {
		            out.close();
		         }
		      }
		   }
		   public void test() {

			   try(BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {

				    StringBuilder sb = new StringBuilder();
				    String line = br.readLine();

				    while (line != null) {
				        sb.append(line);
				        sb.append(System.lineSeparator());
				        line = br.readLine();
				    }
				}
			   catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			   catch (IOException e) {
					e.printStackTrace();
				}
		   }
	}

	public static boolean isRelativePath(String path) {
		java.nio.file.Path p = Paths.get(path);
		return !p.isAbsolute();
	}

}