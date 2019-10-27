package mainPackage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class CommandProcess {
	
	public enum CommandInput {
	    
		quit  	(1),  
	    pwd   	(2),  
	    ls    	(3),
	    mkdir	(4),
	    cd		(5),
	    touch	(6);   

	    private final int commandCode;

	    private CommandInput(int levelCode) {
	        this.commandCode = levelCode;
	    }
	    
	    public int getCommandCode() {
	    	return this.commandCode;
	    }
	}
	
	static class Arbol<T> {
		
		private Nodo<T> raiz;
		
		public Arbol(T nuevaRaiz) {
			raiz = new Nodo<T>(nuevaRaiz, null);
		}

		public Nodo<T> getRaiz(){
			return raiz;
		}
	}
	
	static class Nodo<T> {
		
		private T nodeValue;
		
		private Nodo<T> padre;
		
		private List<Nodo<T>> directorios; 
		
		private List<String> files;
		
		private String fullPath;
		
		public Nodo(T value, Nodo<T> unPadre) {
			nodeValue = value;
			padre = unPadre;
			directorios = new ArrayList<Nodo<T>>();
			files = new ArrayList<String>();
			fullPath = padre == null ? value.toString() : padre.getFullPath().concat("\\").concat(nodeValue.toString()); 
		}
		
		public void addDirectory(T directory, Nodo<T> unPadre) {
			 Nodo<T> nodo = new Nodo<T>(directory, unPadre);
			 directorios.add(nodo);
		}
		
		public void addFile(String file) {
			files.add(file);
		}
		
		public T getNodeValue() {
			return nodeValue;
		}
		
		public List<Nodo<T>> getDirectorios() {
			return directorios;
		}
		
		public List<String> getFiles() {
			return files;
		}
		
		public String getFullPath() {
			return fullPath;
		}
	}
	
	private static Arbol<String> arbol;
	
    private static Nodo<String> workingDirectory;
    
    private static final String BACK_SLASH = "\\";
        
    public static void processInputs(String args[]) {
    
    	arbol = new Arbol<String>("root");
    	workingDirectory = arbol.raiz;
    	
    	int arraySize = args.length;	
    	
    	boolean quit = false;
    	
    	for (int i = 0; !quit &&  i < arraySize; i++) {

    		String input = args[i];
    		
    		CommandInput enumInput = null; 
    		
    		try {
    			enumInput = CommandInput.valueOf(input);
    		}
    		catch (IllegalArgumentException ex) {
    			
    			printUnrecognized();
    			break;
    		}
    		
    		switch (enumInput) {
    			
    			case quit:
    				
    				System.out.println("Exit application");
    				quit = true;
    				break;
    			
    			case pwd:
    				
    				printCurrentDirectory();
    				break;
    			
    			case ls:
    				
    				int nextPosition = processListContent(args, i);
    				
    				i = nextPosition;
    				
         			break;
    			
    			case mkdir:
    				
    				if(!existsNextElement(i, args) || isAReservedValue(args[i + 1]) || args[i + 1].length() > 100) {
        				printUnrecognized();
        				quit = true;
        				break;
        			}
        			mkdir(args[i + 1]);
        			
        			i++;
        			
        			break;
        			
    			case cd:
        			
    				if(!existsNextElement(i, args) || isAReservedValue(args[i + 1])) {
        				printUnrecognized();
        				quit = true;
        				break;
        			}
        			
        			changeDirectory(args[i + 1]);
        			
        			i++;
        			
        			break;
        			
    			case touch:
    				
    				if(!existsNextElement(i, args) || isAReservedValue(args[i + 1]) || args[ i + 1].length() > 100) {
        				printUnrecognized();
        				quit = true;
        				break;
        			}
        			
        			createFile(args[i + 1]);
        			
        			i++;
        			
        			break;
    			default:
    				printUnrecognized();
    		}
    	}
    }

	private static int processListContent(String[] args, int i) {
		
		//el ls puede tener un segundo parámetro
		if(existsNextElement(i, args)){
			
			if(nextParameterIsEqualsTo(args, i, "-r")) {
				
				//recorrer arbol e imprimir hijos.
				inOrden(workingDirectory, workingDirectory.nodeValue);
				
				i++; 
				
			} else if (!isAReservedValue(args[i+1])) {
				Nodo<String> node = getASpecificNode(args[i + 1], arbol.raiz);
				listContent(node);
				
				i++;
			}
			
			else {
				//Si no tiene parámetro extra, imprimimos todo el contenido del directorio actual 	
				listContent(workingDirectory);
			}
			
		} else {
			//Si no tiene parámetro extra, imprimimos todo el contenido del directorio actual 	
			listContent(workingDirectory);
		}	
		
		return i;
	}
    
    private static void createFile(String fileName) {
  
    	if (workingDirectory.files.contains(fileName)) {
    		System.out.println("File already exists");
    	}
    	
    	workingDirectory.addFile(fileName);
    }
    
    private static void changeDirectory(String directory) {
		
    	if (("..").equals(directory) && workingDirectory.padre != null) {
    		workingDirectory = workingDirectory.padre;
    	}else {
    		
    		if (directory.contains(BACK_SLASH)) {
    			processMultiplePaths(directory);
    			
    		}else {

    			Nodo<String> existingNode = getNodeByName(directory, workingDirectory);
    			
    			if(existingNode == null) {
    				System.out.println("Directory not found");
    			}else {
    				workingDirectory = existingNode;	
    			}
    		}
    	}    	
     }
    
    private static void processMultiplePaths(String multiplePaths) {
    	
    	Nodo<String> nodo = getASpecificNode(multiplePaths, workingDirectory);
    	
    	if (nodo != null) {
    		workingDirectory = nodo;
    	} else {
    		System.out.println("Directory not found");
    	}
    }
    
    private static Nodo<String> getASpecificNode(String multiplePaths, Nodo<String> initialDirectory){
    	
    	String pattern = Pattern.quote(BACK_SLASH);
		String[] paths = multiplePaths.substring(1).split(pattern);
		
		boolean existsNode = true;
    	int iterator = 0;
    	int partsSize = paths.length;
    	   	
    	Nodo<String> directory = initialDirectory;
    	
    	while (existsNode && iterator < partsSize) {
    	
    		String node = paths[iterator];
    		
    		Nodo<String> existingNode = getNodeByName(node, directory);
    		
    		if(existingNode == null) {
    			existsNode = false;
    			
    		}else {
    			directory = existingNode;	
    		}
    		
    		iterator ++;
    	}
    	
    	if (existsNode = false) {
    		directory = null;
    	}
    	
    	return directory;
    }
    
    private static void inOrden(Nodo<String> nodo, String currentPath) {
    
    	System.out.println(nodo.fullPath);
    	    	
    	listContent(nodo);    	
    	
    	if(nodo.directorios.isEmpty()) {
    		return;
    	}
    	
    	for(Nodo<String> child : nodo.getDirectorios()) {
    		inOrden(child, currentPath.concat(child.nodeValue));
    	}	
    }
    
    private static void mkdir(String directory) {
    	if(existsDirectory(directory, workingDirectory)) {
    		System.out.println("Directory already exists");
    	}
    	
    	workingDirectory.addDirectory(directory, workingDirectory);
    	
    }
    
    private static boolean existsDirectory(String directoryName, Nodo<String> directory) {

    	Nodo<String> existingDirectory = getNodeByName(directoryName, workingDirectory);
    	
    	return existingDirectory != null;
    }
    
    private static Nodo<String> getNodeByName(String directoryName, Nodo<String> actualNode) {
    	
    	Nodo<String> expectedNode = null;
    	boolean find = false;
    	
    	Iterator<Nodo<String>> iter = actualNode.getDirectorios().iterator();
    	
    	while(!find && iter.hasNext()) {
    		
    		Nodo<String> node = iter.next();
    		
    		if(node.nodeValue.equals(directoryName)) {
    			find = true;
    			expectedNode = node;
    		}
    	}
    	
    	return expectedNode;
    }
    
    private static void listContent(Nodo<String> workingDirectory) {
    	
    	if(!workingDirectory.directorios.isEmpty()) {
    		printFolders(workingDirectory.directorios);
    	}
    	
    	if(!workingDirectory.files.isEmpty()) {
    		printFiles(workingDirectory.files);
    	}
	}

    private static void printFiles(List<String> files) {
    	for(String file : files) {
    		System.out.println(file);
    	}
    }
    
    private static void printFolders(List<Nodo<String>> nodos) {
    	for(Nodo<String> nodo : nodos) {
    		System.out.println(nodo.nodeValue);
    	}
    }
    
	private static boolean nextParameterIsEqualsTo(String[] args, int actualPosition, String comparingValue) {
    	return comparingValue.equals(args[actualPosition + 1]);
    }
	
	private static boolean existsNextElement(int actualPosition, String[] args) {
		return (actualPosition + 1) < args.length;
	}
	
	private static boolean isAReservedValue(String value) {
		return "touch".equals(value) || "mkdir".equals(value) || "cd".equals(value)
				|| "quit".equals(value) || "pwd".equals(value) || "ls".equals(value) || "-r".equals(value);
	}
	
    private static void printUnrecognized() {
    	System.out.println("Unrecognized command");
    }
    
    private static void printCurrentDirectory() {
    	System.out.println("Current directory: " + workingDirectory.fullPath);
    }
}
