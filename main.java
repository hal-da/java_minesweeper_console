import java.util.*;


public class Main {

    public static void main(String[] args) {
        char end = 'y', userAction;
        int[][] field;
        boolean lost = false;
        boolean[][] boolField;
        int rows, cols, mines, mineCounter, userInputX, userInputY, turns;
        start();
        while (end != 'x') {
        
            turns = 0;
            cols = getIntFromUser("Spalten?");
            rows = getIntFromUser("Zeilen?");
            mines = getIntFromUser("Minen?");
            mineCounter = mines;
            field = initField(rows+2, cols+2);
            while (countMines(field)<mines) setABomb(field);
            countNeighbourMines(field);
            boolField=initBoolField(rows+2, cols+2);
            drawField(field, boolField, false);

            while(!lost){
            turns++;

            System.out.println(turns +". Zug. " +mineCounter + " Minen übrig.");
            do{userInputY=getIntFromUser("spalte?");}
              while(userInputY>cols);
            do{userInputX=getIntFromUser("zeile?");}
              while (userInputX>rows);
            userAction=getCharFromUser("Markieren/Markierung entfernen mit'+', aufdecken mit beliebiger Taste.");

            boolField[userInputX][userInputY]=true;
            if(userAction=='+'){
                if(field[userInputX][userInputY]<99){
                    field[userInputX][userInputY]+=100;
                    mineCounter--;
                }else {
                    field[userInputX][userInputY]-=100;
                    boolField[userInputX][userInputY]=false;
                    mineCounter++;
                }
            }else{
                if(field[userInputX][userInputY]==-1){
                    lost=true;
                    drawField(field, boolField, lost);
                    System.out.println("Boom, verloren");
                }else if (field[userInputX][userInputY]==0){
                    countNeighboursZero(field, userInputX, userInputY, boolField);
                }
            }
            if(countMarks(field)==mines&&countMines(field)==0){
                drawField(field, boolField, lost);
                lost=true;
                System.out.println("Yeah, gewonnen!");
            }

            if(!lost)drawField(field, boolField, false);
            }
            lost=false;
            end = getCharFromUser("beliebige Taste für neues Spiel - zum abbrechen 'x' ");
        }
    }

    // gibt nur den starttext aus
    private static void start(){
        System.out.println();
        System.out.println("################################");
        System.out.println("########   MINESWEEPER   #######");
        System.out.println("################## V0.4 DH #####");
        System.out.println();

    }


    //gibt den übergebenen text aus, fragt ein char ab, gibt diesen zurück
    private static char getCharFromUser(String text) {
        Scanner sc = new Scanner(System.in);
        System.out.println(text);
        return sc.next().charAt(0);
    }
    //gibt den übergebenen text aus, fragt ein integer ab, gibt diesen zurück
    private static int getIntFromUser(String text) {
        Scanner sc = new Scanner(System.in);
        System.out.println(text);
        return sc.nextInt();
    }
    //initiert das integer array, füllt mit nullen bzw den rand mit 9 - eventuell redundant
    private static int[][] initField(int rows, int cols) {

        int[][] field = new int[rows][cols];

        for (int i = 0; i <= field.length-1; i++) {
            for (int j = 0; j <= field[i].length-1; j++) {
                if (i == 0 || j == 0 || i == field.length-1 || j == field[i].length-1) {
                    field[i][j] = 9;
                } else field[i][j] = 0;
            }
        }
        return field;
    }
    //initiert das boolsche array, wird nur verwendet um zu überprüfen ob ein feld schon aufgedeckt wurde oder nicht
    private static boolean[][] initBoolField(int rows, int cols){
        boolean[][] boolField= new boolean[rows][cols];

        for (boolean[] booleans : boolField) {
            Arrays.fill(booleans, false);
        }
        return boolField;

    }
    //zeichnet das Feld
    private static void drawField(int[][] field, boolean[][] boolField, boolean lost) {
        drawBorders(field[0].length);
        for (int i = 0; i <= field.length-1; i++) {
            for (int j = 0; j <= field[i].length-1; j++) {
                if(i==0||i==field.length-1)System.out.print("___");
                else if (j==0||j==field[i].length-1)System.out.printf("|%3d|",i);
                else if (boolField[i][j]&&field[i][j]<90&&field[i][j]>0) System.out.printf("%2d ", field[i][j]);
                else if (boolField[i][j] && field[i][j] == 0) System.out.print("   ");
                else if (!lost&&boolField[i][j]&&field[i][j]>10)System.out.print(" X ");
                else if (!lost) System.out.print(" * ");
                else if (field[i][j]==-1||field[i][j]==99) System.out.print(" B ");
                else if (field[i][j]>=100)System.out.print(" O ");
                else if(field[i][j]==0)System.out.print("   ");
                else System.out.printf("%2d ", field[i][j]);

            }
            System.out.println();
        }
        drawBorders(field[0].length);
    }
    //zeichnet das "koordinatensystem" - wird von drawField benutzt
    private static void drawBorders(int col) {
        System.out.print("    ");
        for (int i = 1; i < (col - 1); i++) System.out.printf("%3d", i);
        System.out.println();
    }
    //setzt genau eine bombe
    private static void setABomb(int[][] field){
        Random ra = new Random();
        int x= field.length-2;
        int y= field[0].length-2;
        field[ra.nextInt(x)+1][ra.nextInt(y)+1]=-1;
    }
    //zählt die bomben
    private static int countMines(int[][] field){
        int counter=0;
        for (int[] ints : field) {
            for (int anInt : ints) {
                if (anInt == -1) counter++;
            }
        }
        return counter;
    }
    //zählt für jedes feld die benachbarten minen und schreibt diesen wert ins array
    private static void countNeighbourMines(int[][]field){

        for (int i = 1; i < field.length-1; i++) {
            for (int j = 1; j < field[i].length-1; j++) {
                if (field[i][j] != -1) {
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 2; y++)
                            if (field[i+x][j+y] == -1) field[i][j]++;
                    }
                }
            }
        }


    }
    //zählt die markierten felder - kann man wohl mit countMines kombinieren
    private static int countMarks(int[][]field){
        int counter=0;
        for(int i =1;i<field.length-1;i++){
            for (int j=1; j<field[i].length-1;j++){
                if(field[i][j]==99)counter++;
            }
        }
        return counter;
    }
    //praktisch floodfill - falls übergebenes feld 0 ist wird es und alle nachbarfelder auf true gesetzt, alle nachbarfelder wreden überprüft und genauso behandelt
    private static void countNeighboursZero(int[][]field, int userX, int userY, boolean[][]boolField){

        List<Integer> listX = new ArrayList<>();
        List<Integer> listY = new ArrayList<>();
        listX.add(userX);
        listY.add(userY);

        while (!listX.isEmpty()) {

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (field[listX.get(0) + i][listY.get(0) + j] != -1 && field[listX.get(0) + i][listY.get(0) + j] < 9 && !boolField[listX.get(0) + i][listY.get(0) + j]) {
                        boolField[listX.get(0) + i][listY.get(0) + j] = true;
                        int x = listX.get(0) + i;
                        int y = listY.get(0) + j;
                        if (field[x][y]==0) {
                            listX.add(x);
                            listY.add(y);
                        }
                    }
                }
            }
            listX.remove(0);
            listY.remove(0);
        }
    }
}
