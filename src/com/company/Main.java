package com.company;

import javafx.util.Pair;

import java.util.*;

public class Main {

    static class Agent{
        public char name;
        public int zone;
        public Agent(char name, int zone){
            this.name = name;
            this.zone = zone;
        }
    }

    static class EnvironmentObject{
        public char name;
        public EnvironmentObject(char name){
            this.name = name;
        }

    }

    static class Position {
        public int x;
        public int y;
        public List<Agent> agentList = new ArrayList<>();
        public List<EnvironmentObject> objects = new ArrayList<>();
        public Harry harry = null;
        public boolean inspectorZone;

        public Position(int x, int y){
            this.inspectorZone = false;
            this.x = x;
            this.y = y;
        }

        public char getPosition(){
            if(!agentList.isEmpty()) return agentList.get(agentList.size()-1).name;
            if(inspectorZone) return 'Z';
            if(harry != null) return 'H';
            if(!objects.isEmpty()) return objects.get(objects.size()-1).name;
            return '*';
        }

        public boolean isObject(Class obj){
            for (EnvironmentObject object: objects) {
                if(obj.isInstance(object)) return true;
            }
            return false;
        }


        public void deleteBook(){
            objects.removeIf(object -> object.name == 'B');
        }

        public void deleteCloak(){
            objects.removeIf(object -> object.name == 'I');
        }

    }

    static class ArgusFilch extends Agent{
        public ArgusFilch(){
            super('A',2);
        }
    }

    static class MrsNorris extends Agent{
        public MrsNorris(){
            super('C',1);
        }
    }

    static class Book extends EnvironmentObject{
        public Book(){
            super('B');
        }
    }

    static class Exit extends EnvironmentObject{
        public Exit(){
            super('E');
        }
    }

    static class Cloak extends EnvironmentObject{
        public Cloak(){
            super('I');
        }
    }

    //Actor
    static class Harry{
        public int zone=1;
        public char name = 'H';
        public boolean haveBook = false;
        public boolean haveCloak = false;
        public Harry(){
        }

    }

    static class Map{
        public Position[][] map;
        public Map(){
            this.map = new Position[9][9];
            for(int i=0;i<9;i++){
                for(int k=0;k<9;k++){
                    map[i][k]= new Position(i,k);
                }
            }
        }

        public void setEnemy(Agent enemy, int x, int y){
            for(int i = 0; i<9;i++){
                for(int k = 0; k<9;k++){
                    if(i==x && y == k){
                        map[i][k].agentList.add(enemy);
                        map[i][k].inspectorZone = true;
                    }else if(Math.abs(x-i)<=enemy.zone && Math.abs(y-k)<=enemy.zone){
                        map[i][k].inspectorZone = true;
                    }
                }
            }
        }
        public void setFriend(EnvironmentObject object, int x, int y){
            map[x][y].objects.add(object);
        }

        public void setPlayer(Harry harry, int x, int y){
            map[x][y].harry = harry;
        }

        public void printMap(){
            for(int i=0;i<9;i++){
                for(int k=0;k<9;k++){
                    System.out.print(map[i][k].getPosition()+" ");
                }
                System.out.println();
            }
        }

        public Position findActor(){
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    if(map[i][k].harry!=null) return map[i][k];
                }
            }
            return null;
        }

        public Position findBook(){
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    if(map[i][k].objects!=null) return map[i][k];
                }
            }
            return null;
        }

        public void DeleteInspector(){
            for(int i=0;i<9;i++){
                for (int k = 0; k < 9; k++) {
                    if(map[i][k].inspectorZone && map[i][k].agentList.isEmpty()) map[i][k].inspectorZone=false;
                }
            }

        }


    }

    static class Game{
        private Harry harry;
        private Book book;
        private Exit exit;
        private Cloak cloak;
        private ArgusFilch argusFilch;
        private MrsNorris mrsNorris;
        private Map gameGrid;
        public Game(){
            harry = new Harry();//(new Position(8,0));
            book = new Book();//(new Position(4,7));
            exit = new Exit();//(new Position(4,1));
            argusFilch = new ArgusFilch();//(new Position(6,4));
            mrsNorris = new MrsNorris();//(new Position(1,2));
            cloak = new Cloak();//(new Position(0,0));

            gameGrid = new Map();

            gameGrid.setEnemy(argusFilch,6,5);
            gameGrid.setEnemy(mrsNorris,2,1);
            gameGrid.setFriend(book,4,7);
            gameGrid.setFriend(cloak,0,0);
            gameGrid.setFriend(exit,4,1);
            gameGrid.setPlayer(harry,8,0);
            gameGrid.printMap();

        }

        public void startBackTracking(boolean printStep){
            BackTracking backTracking = new BackTracking(gameGrid, harry);
            backTracking.printStep=printStep;
            if(!backTracking.DFS()){
                System.out.println("GAME OVER");
            }else{
                System.out.println("FINISH "+backTracking.path.size());
            }

            backTracking.printStack();
        }

    }




    static class BackTracking{
        private Map grid;
        private Harry harry;
        public boolean printStep = false;
        private Stack<Position> path = new Stack<>();
        public BackTracking(Map gameMap, Harry harry){
            this.grid = gameMap;
            this.harry = harry;
        }

        private boolean DFSUtil(int x, int y, boolean[][] visited){
            int H = grid.map.length;
            int L = grid.map[0].length;
            path.push(new Position(x,y));
            if(x<0 || y<0 || x>=H || y>=L || visited[x][y]|| grid.map[x][y].inspectorZone){
                return false;
            }

            if(grid.map[x][y].isObject(Cloak.class)){
                harry.haveCloak=true;
                grid.DeleteInspector();
                grid.map[x][y].deleteCloak();
                visited = new boolean[H][L];
            }
            if(grid.map[x][y].isObject(Book.class)){
                harry.haveBook = true;
                grid.map[x][y].deleteBook();
                return true;
            }
            if(grid.map[x][y].isObject(Exit.class) && harry.haveBook){
                return true;
            }

            grid.map[x][y].harry = harry;
            visited[x][y]=true;
            if(printStep)grid.printMap();
            if(printStep)System.out.println();
            grid.map[x][y].harry = null;
            if(DFSUtil(x+1,y,visited)) return true;
            path.pop();
            if(DFSUtil(x-1,y,visited)) return true;
            path.pop();
            if(DFSUtil(x,y+1,visited)) return true;
            path.pop();
            if(DFSUtil(x,y-1,visited)) return true;
            path.pop();
            if(DFSUtil(x+1,y+1,visited)) return true;
            path.pop();
            if(DFSUtil(x+1,y-1,visited)) return true;
            path.pop();
            if(DFSUtil(x-1,y+1,visited)) return true;
            path.pop();
            if(DFSUtil(x-1,y-1,visited)) return true;
            path.pop();
            return false;
        }

        public boolean DFS(){
            Position player = grid.findActor();
            int h = grid.map.length;
            int l = grid.map[player.x].length;
            if(printStep) System.out.println("DFS book:");
            boolean result = DFSUtil(player.x, player.y, new boolean[h][l]);
            if(!result){
                return false;
            }
            Stack<Position> partPath = new Stack<>();
            partPath.addAll(path);
            path.clear();
            if(printStep) System.out.println("\nDFS exit:");
            DFSUtil(partPath.peek().x, partPath.peek().y, new boolean[h][l]);
            partPath.pop();
            partPath.addAll(path);
            path.clear();
            path.addAll(partPath);
            return true;
        }

        private void printStack(Stack<Position> pathPrint){
            for (Position pos: pathPrint) {
                System.out.print("["+pos.x+", "+ pos.y+"] ");
            }
        }

        public void printStack(){
           printStack(path);
        }

    }

    static class Dijkstra{
        Map grid;
        Harry harry;
        List<Pair<Integer, Position>> wave = new ArrayList<>();
        public Dijkstra(Map gameMap, Harry harry){
            this.grid = gameMap;
            this.harry = harry;
        }

        private void ShortestPath(){
            Position player = grid.findActor();
        }

        private void LeeAlgorithm(int x, int y, boolean[][] visited, int[][] waves){
            int H = grid.map.length;
            int L = grid.map[0].length;

            if(x<)

            if(x==grid.findActor().x && y == grid.findActor().y){
                waves[x][y]=0;
            }


        }

        private boolean findWay(){

        }
    }



    public static void main(String[] args) {
    Game game = new Game();
    game.startBackTracking(true);
    }
}
