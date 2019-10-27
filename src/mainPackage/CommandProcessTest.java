package mainPackage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class CommandProcessTest {

	private static final String UNRECOGNIZED = "Unrecognized command\r\n";
	
	class TestHelper {

	    public void captureOutput( CaptureTest test ) throws Exception {
	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	        System.setOut(new PrintStream(outContent));
	        System.setErr(new PrintStream(errContent));

	        test.test( outContent, errContent );

	        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.out)));

	    }
	}

	abstract class CaptureTest {
	    public abstract void test( ByteArrayOutputStream outContent, ByteArrayOutputStream errContent ) throws Exception;
	}

	
	@Test
	void unrecognizedCommand() throws Exception {
		 
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[1];
    			arguments[0] = "WRONG_COMMAND";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals(UNRECOGNIZED, outContent.toString());
            }
        });
	}

	@Test
	void emptyList() throws Exception {
		 
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
    			CommandProcess.processInputs(new String[0]);
    			
                assertEquals("", outContent.toString());
            }
        });
	}
	
	@Test
	void mainFolderIsEmpty() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[1];
    			arguments[0] = "ls";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("", outContent.toString());
            }
        });	
	}
	
	@Test
	void addNewFolderInRoot() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[3];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "ls";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("New Folder\r\n", outContent.toString());
            }
        });	
	}
	
	@Test
	void addDirectoryButAlreadyExists() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[4];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "mkdir";
    			arguments[3] = "New Folder";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("Directory already exists\r\n", outContent.toString());
            }
        });	
	}
	
	@Test
	void addFileInNewFolder() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[7];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "cd";
    			arguments[3] = "New Folder";
    			arguments[4] = "touch";
    			arguments[5] = "a beautiful file";
    			arguments[6] = "ls";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("a beautiful file\r\n", outContent.toString());
            }
        });	
	}
	
	@Test
	void addFileInNewFolderButAlreadyExists() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[6];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "touch";
    			arguments[3] = "a beautiful file";
    			arguments[4] = "touch";
    			arguments[5] = "a beautiful file";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("File already exists\r\n", outContent.toString());
            }
        });	
	}
	
	@Test
	void createFolderInNewFolder() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[7];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "cd";
    			arguments[3] = "New Folder";
    			arguments[4] = "mkdir";
    			arguments[5] = "Special Folder";
    			arguments[6] = "ls";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("Special Folder\r\n", outContent.toString());
            }
        });	
	}
	
	@Test
	void createFileInSecondLevel() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[11];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "cd";
    			arguments[3] = "New Folder";
    			arguments[4] = "mkdir";
    			arguments[5] = "Special Folder";
    			arguments[6] = "cd";
    			arguments[7] = "Special Folder";
    			arguments[8] = "touch";
    			arguments[9] = "Work File";
    			arguments[10] = "ls";
    			
    			CommandProcess.processInputs(arguments);
    			
                assertEquals("Work File\r\n", outContent.toString());
            }
        });	
	}
	
	@Test
	void listRecursiveTwoLevels() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[16];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "cd";
    			arguments[3] = "New Folder";
    			arguments[4] = "mkdir";
    			arguments[5] = "Special Folder";
    			arguments[6] = "cd";
    			arguments[7] = "Special Folder";
    			arguments[8] = "touch";
    			arguments[9] = "Work File";
    			arguments[10] = "cd";
    			arguments[11] = "..";
    			arguments[12] = "cd";
    			arguments[13] = "..";
    			arguments[14] = "ls";
    			arguments[15] = "-r";
    			
    			CommandProcess.processInputs(arguments);
    			
    			String expectedOutput = "root\r\n";
    			expectedOutput += "New Folder\r\n";
    			expectedOutput += "root\\New Folder\r\n";
    			expectedOutput += "Special Folder\r\n";
    			expectedOutput += "root\\New Folder\\Special Folder\r\n";
    			expectedOutput += "Work File\r\n";
    			
                assertEquals(expectedOutput, outContent.toString());
            }
        });	
	}
	
	@Test
	void listSpecificPath() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[16];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "cd";
    			arguments[3] = "New Folder";
    			arguments[4] = "mkdir";
    			arguments[5] = "Special Folder";
    			arguments[6] = "cd";
    			arguments[7] = "Special Folder";
    			arguments[8] = "touch";
    			arguments[9] = "Work File";
    			arguments[10] = "cd";
    			arguments[11] = "..";
    			arguments[12] = "cd";
    			arguments[13] = "..";
    			arguments[14] = "ls";
    			arguments[15] = "\\New Folder\\Special Folder";
    			
    			CommandProcess.processInputs(arguments);
    			
    			String expectedOutput = "Work File\r\n";
    			
                assertEquals(expectedOutput, outContent.toString());
            }
        });	
	}
	
	@Test
	void parentDirectoryInRoot() throws Exception {
		 
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arrays = new String[3];
            	
            	arrays[0] = "cd";
            	arrays[1] = "..";
            	arrays[2] = "ls";
            	
    			CommandProcess.processInputs(new String[0]);
    			
                assertEquals("", outContent.toString());
            }
        });
	}
	
	@Test
	void changeToASpecificDirectory() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[23];
    			arguments[0] = "mkdir";
    			arguments[1] = "New Folder";
    			arguments[2] = "cd";
    			arguments[3] = "New Folder";
    			arguments[4] = "mkdir";
    			arguments[5] = "Special Folder";
    			arguments[6] = "cd";
    			arguments[7] = "Special Folder";
    			arguments[8] = "mkdir";
    			arguments[9] = "third level Folder";
    			arguments[10] = "cd";
    			arguments[11] = "third level Folder";
    			arguments[12] = "touch";
    			arguments[13] = "Work File";
    			arguments[14] = "cd";
    			arguments[15] = "..";
    			arguments[16] = "cd";
    			arguments[17] = "..";
    			arguments[18] = "cd";
    			arguments[19] = "..";
    			arguments[20] = "cd";
    			arguments[21] = "\\New Folder\\Special Folder\\third level Folder";
    			arguments[22] = "ls";
    			
    			CommandProcess.processInputs(arguments);
    			
    			String expectedOutput = "Work File\r\n";
    			
                assertEquals(expectedOutput, outContent.toString());
            }
        });	
	}
	
	@Test
	void folderNameWithMoreThaOneHundredChars() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[2];
    			arguments[0] = "mkdir";
    			arguments[1] = getMoreThanOneHundrerChars();
    			
    			
    			CommandProcess.processInputs(arguments);
    			
    			String expectedOutput = UNRECOGNIZED;
    			
                assertEquals(expectedOutput, outContent.toString());
            }
        });	
	}
	
	@Test
	void fileNameWithMoreThanHundredChars() throws Exception {
		
		TestHelper helper = new TestHelper();
		
		helper.captureOutput( new CaptureTest() {
			
            @Override
            public void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent) throws Exception {
            	
            	String[] arguments = new String[2];
    			arguments[0] = "touch";
    			arguments[1] = getMoreThanOneHundrerChars();
    			
    			
    			CommandProcess.processInputs(arguments);
    			
    			String expectedOutput = UNRECOGNIZED;
    			
                assertEquals(expectedOutput, outContent.toString());
            }
        });	
	}
	
	 private String getMoreThanOneHundrerChars() {
     	String result = "";
     	
     	for (int i = 0; i < 101; i++) {
     		result += "a";
     	}
     	
     	return result;
     }
}
