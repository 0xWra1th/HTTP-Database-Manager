//IMPORTS
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class Server{

    // ------ GLOBAL VARIABLES ------
    private static ServerSocket serverSocket;
    private static int port;
    private static boolean running = false;
    private static String RESULTS = "";
    private static String[] answerCalculator1 = new String[0];
    private static String[] answerCalculator2 = new String[0];
    // ------------------------------

    public static void main(String[] args){
        //Initialise variables
        port = Integer.parseInt(args[0]);

        //Run Server
        setupServer();
        serverLoop();
    }

    private static void serveWebsite(Socket sock){
        try{
        // ---------- CONVERT WEBSITE WITH NEW ANSWER TO STRING ----------
            File html = new File("index.html");
            Scanner scan = new Scanner(html);
            String res = "HTTP/1.1 200 OK"+"\n"+
                        "Content-Type: text/html\n"+
                        "Server: Friendly Phones"+"\n"+
                        "Host: localhost:"+port+"\n"+
                        "X-Powered-By: Java\n\n";
            int count = 0;
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.contains("+--") || line.contains("SEARCH RESULTS")){
                    //System.out.println(line);
                    count++;
                }else if(count == 3){
                    String head = "+----------------+\n"+"| SEARCH RESULTS |\n"+"+----------------+\n";
                    res = res+head+RESULTS+"\n";
                    res = res+line+"\n";
                    count = 0;
                }else{
                    res = res+line+"\n";
                }
            }
        // ---------------------------------------------------------------
        // ----------- SEND WEBSITE IN HTTP RESPONSE TO CLIENT -----------
            try{
                PrintWriter out = new PrintWriter(sock.getOutputStream());
                out.print(res);
                out.flush();
            }catch(Exception e){
                e.printStackTrace();
            }
        // ---------------------------------------------------------------
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void serverLoop(){
        // ----------- WAIT FOR CONNECTION, READ REQUESTS AND HANDLE THEM -----------
        while(running){
            try{
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // -------- Handle Request --------
                handleRequest(in, socket);
                // --------------------------------
                
            }catch(Exception e){
                running = false;
                e.printStackTrace();
            }
        }
        // --------------------------------------------------------------------------
    }

    private static void setupServer(){
        // ----------- CREATE SERVER SOCKETS AND BIND TO PORT -----------
        try{
            serverSocket = new ServerSocket(port);
            running = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        if(running){
            System.out.println("Server running on port: "+port);
        }else{
            System.out.println("ERROR: Server failed to start on port: "+port+"!");
        }
        // --------------------------------------------------------------
    }

    private static void handleInput(String input){
        // ------------------ HANDLE GET REQUEST INPUT ------------------
        try{
            
        }catch(Exception e){
            e.printStackTrace();
        }
        // --------------------------------------------------------------
    }

    private static void handleRequest(BufferedReader in, Socket sock){
        // ------------ READ THROUGH HEADERS AND DETERMINE ACTIONS ------------
        try{
            String line = in.readLine();
            System.out.println("---------------------------------------");
            while(line != null && line.length() > 0){
                System.out.println(line);
                String method = line.split(" ")[0];
                String url = line.split(" ")[1];
                String action = url.split("[?]")[0];
                String[] args = new String[4];
                if(method.equals("GET")){
                    if(url.equals("/favicon.ico")){ //IGNORE FAVICON REQUESTS...
                        break;
                    }else if(action.equals("/insert")){
                        //System.out.println("-----------\nINSERT");
                        args = url.split("[?]")[1].split("[&]");
                        int arg1len = args[0].split("[=]").length;
                        int arg2len = args[1].split("[=]").length;
                        if(arg1len != 1 && arg2len != 1){
                            insert(args[0].split("[=]")[1], args[1].split("[=]")[1]);
                        }else if(arg1len != 1){
                            insert(args[0].split("[=]")[1], "");
                        }else if(arg2len != 1){
                            insert("", args[1].split("[=]")[1]);
                        }else{
                            insert("", "");
                        }                  
                        serveWebsite(sock);
                        break;
                    }else if(action.equals("/update")){
                        //System.out.println("-----------\nUPDATE");
                        args = url.split("[?]")[1].split("[&]");
                        int arg1len = args[0].split("[=]").length;
                        int arg2len = args[1].split("[=]").length;
                        int arg3len = args[2].split("[=]").length;
                        int arg4len = args[3].split("[=]").length;
                        if(arg1len != 1 && arg2len != 1 && arg3len != 1 && arg4len != 1){
                            update(args[0].split("[=]")[1], args[1].split("[=]")[1], args[2].split("[=]")[1], args[3].split("[=]")[1]);
                        }else if(arg1len != 1 && arg2len != 1 && arg3len != 1){
                            update(args[0].split("[=]")[1], args[1].split("[=]")[1], args[2].split("[=]")[1], "");
                        }else if(arg1len != 1 && arg2len != 1 && arg4len != 1){
                            update(args[0].split("[=]")[1], args[1].split("[=]")[1], "", args[3].split("[=]")[1]);
                        }else if(arg1len != 1 && arg3len != 1 && arg4len != 1){
                            update(args[0].split("[=]")[1], "", args[2].split("[=]")[1], args[3].split("[=]")[1]);
                        }else if(arg2len != 1 && arg3len != 1 && arg4len != 1){
                            update("", args[1].split("[=]")[1], args[2].split("[=]")[1], args[3].split("[=]")[1]);
                        }else if(arg1len != 1 && arg2len != 1){
                            update(args[0].split("[=]")[1], args[1].split("[=]")[1], "", "");
                        }else if(arg1len != 1 && arg3len != 1){
                            update(args[0].split("[=]")[1], "", args[2].split("[=]")[1], "");
                        }else if(arg1len != 1 && arg4len != 1){
                            update(args[0].split("[=]")[1], "", "", args[3].split("[=]")[1]);
                        }else if(arg2len != 1 && arg3len != 1){
                            update("", args[1].split("[=]")[1], args[2].split("[=]")[1], "");
                        }else if(arg2len != 1 && arg4len != 1){
                            update("", args[1].split("[=]")[1], "", args[3].split("[=]")[1]);
                        }else if(arg3len != 1 && arg4len != 1){
                            update("", "", args[2].split("[=]")[1], args[3].split("[=]")[1]);
                        }else if(arg1len != 1){
                            update(args[0].split("[=]")[1], "", "", "");
                        }else if(arg2len != 1){
                            update("", args[1].split("[=]")[1], "", "");
                        }else if(arg3len != 1){
                            update("", "", args[2].split("[=]")[1], "");
                        }else if(arg4len != 1){
                            update("", "", "", args[3].split("[=]")[1]);
                        }else{
                            update("", "", "", "");
                        }
                        serveWebsite(sock);
                        break;

                    }else if(action.equals("/delete")){
                        //System.out.println("-----------\nDELETE");
                        args = url.split("[?]")[1].split("[&]");
                        int arg1len = args[0].split("[=]").length;
                        int arg2len = args[1].split("[=]").length;
                        if(arg1len != 1 && arg2len != 1){
                            delete(args[0].split("[=]")[1], args[1].split("[=]")[1]);
                        }else if(arg1len != 1){
                            delete(args[0].split("[=]")[1], "");
                        }else if(arg2len != 1){
                            delete("", args[1].split("[=]")[1]);
                        }else{
                            delete("", "");
                        }                  
                        serveWebsite(sock);
                        break;
                    }else if(action.equals("/search")){
                        //System.out.println("-----------\nSEARCH");
                        args = url.split("[?]")[1].split("[&]");
                        int arg1len = args[0].split("[=]").length;
                        int arg2len = args[1].split("[=]").length;
                        if(arg1len != 1 && arg2len != 1){
                            search(args[0].split("[=]")[1], args[1].split("[=]")[1]);
                        }else if(arg1len != 1){
                            search(args[0].split("[=]")[1], "");
                        }else if(arg2len != 1){
                            search("", args[1].split("[=]")[1]);
                        }else{
                            search("", "");
                        }                  
                        serveWebsite(sock);
                        break;
                    }else{
                        serveWebsite(sock);
                        break;
                    }
                }else if(method.equals("HEAD")){
                    // SEND SERVER INFORMATION IN HEADERS
                    handleHead(sock);
                    break;
                }else if(method.equals("OPTIONS")){
                    // SEND AVAILABLE HTTP METHODS IN HEADERS
                    handleOptions(sock);
                    break;
                }
            }
            System.out.println("---------------------------------------");
        }catch(Exception e){
            e.printStackTrace();
        }
        // --------------------------------------------------------------------
    }

    private static void handleHead(Socket s){
    	// ----------- SEND HEADERS IN HTTP RESPONSE TO CLIENT -----------
    	String res = 	"HTTP/1.1 200 OK"+"\n"+
    					"Server: Friendly Phones"+"\n"+
    					"Host: localhost:"+port+"\n"+
    					"X-Powered-By: Java\n\n";
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

    private static void handleOptions(Socket s){
    	// ----------- SEND OPTIONS IN HTTP RESPONSE TO CLIENT -----------
    	String res = 	"HTTP/1.1 200 OK"+"\n"+
    					"Allow: GET HEAD OPTIONS"+"\n"+
    					"Server: Friendly Phones"+"\n"+
    					"Host: localhost:"+port+"\n"+
    					"X-Powered-By: Java\n\n";
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }
    // ---------------------- CRUD FUNCIONALITY ----------------------
    private static void search(String name, String number){
        // ---- DECODE URL PARAMETERS ----
        try{
            name = URLDecoder.decode(name, "UTF-8" );
            number = URLDecoder.decode(number, "UTF-8" );
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // -------- READ IN DATA ---------
        ArrayList<String> data = new ArrayList<String>();
        try{
            File database = new File("database.txt");
            Scanner scan = new Scanner(database);
            while(scan.hasNext()){
                data.add(scan.nextLine());
            }
            scan.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // --------- SEARCH DATA ---------
        RESULTS = "";
        for(int i=0;i<data.size();i++){
            //System.out.println("NAME: "+name+" -> "+data.get(i).split("[|]")[0]);
            //System.out.println("NUMBER: "+number+" -> "+data.get(i).split("[|]")[1]);
            if((name.equals("") || number.equals("")) && (data.get(i).split("[|]")[0].equals(name) || data.get(i).split("[|]")[1].equals(number))){
                RESULTS = RESULTS+data.get(i).split("[|]")[0]+"\t\t|  "+data.get(i).split("[|]")[1]+"\n";
            }else if(name.equals("") && number.equals("")){
                RESULTS = RESULTS+data.get(i).split("[|]")[0]+"\t\t|  "+data.get(i).split("[|]")[1]+"\n";
            }else if(data.get(i).split("[|]")[0].equals(name) && data.get(i).split("[|]")[1].equals(number)){
                RESULTS = RESULTS+data.get(i).split("[|]")[0]+"\t\t|  "+data.get(i).split("[|]")[1]+"\n";
            }
        }
        // -------------------------------
    }
    private static void insert(String name, String number){
        // ---- DECODE URL PARAMETERS ----
        try{
            name = URLDecoder.decode(name, "UTF-8" );
            number = URLDecoder.decode(number, "UTF-8" );
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // -------- READ IN DATA ---------
        ArrayList<String> data = new ArrayList<String>();
        try{
            File database = new File("database.txt");
            Scanner scan = new Scanner(database);
            while(scan.hasNext()){
                data.add(scan.nextLine());
            }
            scan.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // --------- INSERT DATA ---------
        data.add(name+"|"+number);
        // -------------------------------

        // --------- OUTPUT DATA ---------
        try{
            FileWriter fw = new FileWriter("database.txt");
            for(int i=0;i<data.size();i++){
                fw.write(data.get(i)+"\n");
            }
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        search("","");
        // -------------------------------
    }
    private static void update(String oName, String oNumber, String nName, String nNumber){
        // ---- DECODE URL PARAMETERS ----
        try{
            oName = URLDecoder.decode(oName, "UTF-8" );
            oNumber = URLDecoder.decode(oNumber, "UTF-8" );
            nName = URLDecoder.decode(nName, "UTF-8" );
            nNumber = URLDecoder.decode(nNumber, "UTF-8" );
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // -------- READ IN DATA ---------
        ArrayList<String> data = new ArrayList<String>();
        try{
            File database = new File("database.txt");
            Scanner scan = new Scanner(database);
            while(scan.hasNext()){
                data.add(scan.nextLine());
            }
            scan.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // --------- UPDATE DATA ---------
        for(int i=0;i<data.size();i++){
            if(((data.get(i).split("[|]")[0].equals(oName) && data.get(i).split("[|]")[1].equals(oNumber)) || (oName.equals("") && data.get(i).split("[|]")[1].equals(oNumber)) || (oNumber.equals("") && data.get(i).split("[|]")[0].equals(oName)))){
                if(nName.equals("") && !nNumber.equals("")){
                    String oldName = data.get(i).split("[|]")[0];
                    data.set(i, nName+"|"+oldName);
                }else if(nNumber.equals("") && !nName.equals("")){
                    String oldNumber = data.get(i).split("[|]")[1];
                    data.set(i, nName+"|"+oldNumber);
                }else if(!nName.equals("") && !nNumber.equals("")){
                    data.set(i, nName+"|"+nNumber);
                }else{
                    data.remove(i);
                }
            }
        }
        // -------------------------------

        // --------- OUTPUT DATA ---------
        try{
            FileWriter fw = new FileWriter("database.txt");
            for(int i=0;i<data.size();i++){
                fw.write(data.get(i)+"\n");
            }
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        search("", "");
        // -------------------------------
    }
    private static void delete(String name, String number){
        // ---- DECODE URL PARAMETERS ----
        try{
            name = URLDecoder.decode(name, "UTF-8" );
            number = URLDecoder.decode(number, "UTF-8" );
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // -------- READ IN DATA ---------
        ArrayList<String> data = new ArrayList<String>();
        try{
            File database = new File("database.txt");
            Scanner scan = new Scanner(database);
            while(scan.hasNext()){
                data.add(scan.nextLine());
            }
            scan.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        // -------------------------------

        // --------- DELETE DATA ---------
        for(int i=0;i<data.size();i++){
            if(((data.get(i).split("[|]")[0].equals(name) && data.get(i).split("[|]")[1].equals(number)) || (name.equals("") && data.get(i).split("[|]")[1].equals(number)) || (number.equals("") && data.get(i).split("[|]")[0].equals(name)))){
                data.remove(i);
            }
        }
        // -------------------------------

        // --------- OUTPUT DATA ---------
        try{
            FileWriter fw = new FileWriter("database.txt");
            for(int i=0;i<data.size();i++){
                fw.write(data.get(i)+"\n");
            }
            fw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        search("", "");
        // -------------------------------
    }
    // ---------------------------------------------------------------


}