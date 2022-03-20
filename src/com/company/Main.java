package com.company;

import java.util.*;

public class Main {

    static class Agent {
        public char name;
        public int zone;

        public Agent(char name, int zone) {
            this.name = name;
            this.zone = zone;
        }

        public Agent Copy(Agent anotherAgent) {
            return new Agent(anotherAgent.name, anotherAgent.zone);
        }
    }

    static class EnvironmentObject {
        public char name;

        public EnvironmentObject(char name) {
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

        public Position(int x, int y) {
            this.inspectorZone = false;
            this.x = x;
            this.y = y;
        }

        public Position(Position newPosition) {
            this.inspectorZone = newPosition.inspectorZone;
            this.x = newPosition.x;
            this.y = newPosition.y;
            this.objects = new ArrayList<>(newPosition.objects);
            this.agentList = new ArrayList<>(newPosition.agentList);
            if (newPosition.harry != null) {
                this.harry = new Harry(newPosition.harry);
            } else {
                this.harry = null;
            }
        }

        public char getPosition() {
            if (harry != null) return 'H';
            if (!agentList.isEmpty()) return agentList.get(agentList.size() - 1).name;
            if (inspectorZone) return 'Z';
            if (!objects.isEmpty()) return objects.get(objects.size() - 1).name;
            return '*';
        }

        public boolean isObject(Class obj) {
            for (EnvironmentObject object : objects) {
                if (obj.isInstance(object)) return true;
            }
            return false;
        }


        public void deleteBook() {
            objects.removeIf(object -> object.name == 'B');
        }

        public void deleteCloak() {
            objects.removeIf(object -> object.name == 'I');
        }


    }

    static class ArgusFilch extends Agent {
        public ArgusFilch() {
            super('A', 2);
        }
    }

    static class MrsNorris extends Agent {
        public MrsNorris() {
            super('C', 1);
        }
    }

    static class Book extends EnvironmentObject {
        public Book() {
            super('B');
        }
    }

    static class Exit extends EnvironmentObject {
        public Exit() {
            super('E');
        }
    }

    static class Cloak extends EnvironmentObject {
        public Cloak() {
            super('I');
        }
    }

    //Actor
    static class Harry {
        public int zone = 1;
        public char name = 'H';
        public boolean haveBook = false;
        public boolean haveCloak = false;

        public Harry() {
        }

        public Harry(Harry anotherHarry) {
            this.zone = anotherHarry.zone;
            this.name = anotherHarry.name;
            this.haveBook = anotherHarry.haveBook;
            this.haveCloak = anotherHarry.haveCloak;
        }

        private boolean isDangerous(Position position) {
            return position.inspectorZone;
        }
    }

    static class Map {
        public Position[][] map;

        public Map() {
            this.map = new Position[9][9];
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    map[i][k] = new Position(i, k);
                }
            }
        }

        public Map(Map anotherMap) {
            this.map = new Position[9][9];
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    this.map[i][k] = new Position(anotherMap.map[i][k]);
                }
            }
        }

        public void setEnemy(Agent enemy, int x, int y) {
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    if (i == x && y == k) {
                        map[i][k].agentList.add(enemy);
                        map[i][k].inspectorZone = true;
                    } else if (Math.abs(x - i) <= enemy.zone && Math.abs(y - k) <= enemy.zone) {
                        map[i][k].inspectorZone = true;
                    }
                }
            }
        }

        public void setFriend(EnvironmentObject object, int x, int y) {
            map[x][y].objects.add(object);
        }

        public void setPlayer(Harry harry, int x, int y) {
            map[x][y].harry = harry;
        }

        public void printMap() {
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    System.out.print(map[i][k].getPosition() + " ");
                }
                System.out.println();
            }
        }

        public Position findActor() {
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    if (map[i][k].harry != null) return map[i][k];
                }
            }
            return null;
        }


        public void findCloak() {
            for (int i = 0; i < 9; i++) {
                for (int k = 0; k < 9; k++) {
                    if (map[i][k].inspectorZone && map[i][k].agentList.isEmpty()) map[i][k].inspectorZone = false;
                }
            }

        }


    }

    static class Node {
        int x, y, dist;

        public Node(int x, int y, int dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }

        public Node(Node node) {
            this.x = node.x;
            this.y = node.y;
            this.dist = node.dist;
        }
    }

    static class Game {
        private final Harry harry = new Harry();
        private final Book book = new Book();
        private final Exit exit = new Exit();
        private final Cloak cloak = new Cloak();
        private final ArgusFilch argusFilch = new ArgusFilch();
        private final MrsNorris mrsNorris = new MrsNorris();
        private final Map gameGrid = new Map();

        public Game() {
            gameGrid.setEnemy(argusFilch, 6, 4);
            gameGrid.setEnemy(mrsNorris, 1, 2);
            gameGrid.setFriend(book, 4, 7);
            gameGrid.setFriend(cloak, 0, 0);
            gameGrid.setFriend(exit, 4, 1);
            gameGrid.setPlayer(harry, 8, 3);
            gameGrid.printMap();

        }

        public Game(int type){

            switch (type){
                case(0):
                    RandomGenerate();
                    break;
                case(1):
                    ManualInput();
                    break;
            }

            gameGrid.printMap();
        }

        private void ManualInput(){
            Scanner sc = new Scanner(System.in);
            while (true){
                try {
                    String[] coordinates = sc.nextLine().split(" ");
                    String perception = sc.nextLine();
                    int[][] intCoord = new int[6][2];
                    int i=0;
                    for (String strings: coordinates) {
                        strings = strings.replace("[","").replace("]","");
                        intCoord[i][0] = Integer.parseInt(strings.split(",")[0]);
                        intCoord[i][1] = Integer.parseInt(strings.split(",")[1]);
                        i++;
                    }
                    gameGrid.setPlayer(harry,Math.abs(intCoord[0][1]-8),intCoord[0][0]);
                    gameGrid.setEnemy(argusFilch,Math.abs(intCoord[1][1]-8),intCoord[1][0]);
                    gameGrid.setEnemy(mrsNorris,Math.abs(intCoord[2][1]-8),intCoord[2][0]);
                    if(gameGrid.map[Math.abs(intCoord[3][1]-8)][intCoord[3][0]].inspectorZone) throw new Exception();
                    gameGrid.setFriend(book,Math.abs(intCoord[3][1]-8),intCoord[3][0]);
                    if(gameGrid.map[Math.abs(intCoord[4][1]-8)][intCoord[4][0]].inspectorZone) throw new Exception();
                    gameGrid.setFriend(cloak,Math.abs(intCoord[4][1]-8),intCoord[4][0]);
                    if(gameGrid.map[Math.abs(intCoord[5][1]-8)][intCoord[5][0]].inspectorZone) throw new Exception();
                    gameGrid.setFriend(exit,Math.abs(intCoord[5][1]-8),intCoord[5][0]);

                    if(Integer.parseInt(perception) == 1){
                        startBackTracking(true);
                        startDijkstra(false);
                    }

                }catch (Exception ex){
                    System.out.println("Invalid data, input again");
                }
            }
        }

        private void RandomGenerate(){
            gameGrid.setPlayer(harry, 8, 0);
            Random random = new Random();


            int x = random.nextInt(9);
            int y = random.nextInt(9);

            gameGrid.setEnemy(argusFilch, x, y);

            x = random.nextInt(9);
            y= random.nextInt(9);

            gameGrid.setEnemy(mrsNorris, x, y);

            x = random.nextInt(9);
            y= random.nextInt(9);

            while(gameGrid.map[x][y].inspectorZone){
                x = random.nextInt(9);
                y= random.nextInt(9);
            }
            gameGrid.setFriend(exit, x, y);

            x = random.nextInt(9);
            y= random.nextInt(9);

            while(gameGrid.map[x][y].inspectorZone && gameGrid.map[x][y].isObject(Exit.class) && gameGrid.map[x][y].harry!=null){
                x = random.nextInt(9);
                y= random.nextInt(9);
            }
            gameGrid.setFriend(book, x,y);

            x= random.nextInt(9);
            y= random.nextInt(9);

            while(gameGrid.map[x][y].inspectorZone){
                x = random.nextInt(9);
                y= random.nextInt(9);
            }
            gameGrid.setFriend(cloak, x, y);

        }

        public void startBackTracking(boolean printStep) {
            BackTracking backTracking = new BackTracking(new Map(gameGrid));
            backTracking.printStep = printStep;
            if (!backTracking.DFS()) {
                System.out.println("No path");
            } else {
                backTracking.printStack();
                System.out.print(" "+(backTracking.path.size()-1));
            }

        }

        public void startDijkstra(boolean pathShow) {
            Dijkstra dijkstra = new Dijkstra(new Map(gameGrid));
            Stack<Position> path = dijkstra.findMinDistance();
            if(path == null){
                System.out.println("No path");
            }else{
                if(pathShow){

                }
                dijkstra.printStack(path);
                System.out.print(" "+(path.size() - 1));
            }
        }

        public boolean startDijkstraStat() {
            Dijkstra dijkstra = new Dijkstra(new Map(gameGrid));
            Stack<Position> path = dijkstra.findMinDistance();
            if(path == null){
                return false;
            }else{
                return true;
            }
        }

        public void startSecondPerceptionBackTracking(boolean print){
            System.out.println("\n");
            BackTrackingPrecision2 backTrackingPrecision2 = new BackTrackingPrecision2(gameGrid, harry,print);
            backTrackingPrecision2.DFS();
        }

        public void printPathWithMap(Map map, Stack<Position> path){
            Harry myharry = new Harry(harry);
            for (Position pos: path) {
                map.map[map.findActor().x][map.findActor().y].harry=null;
                map.map[pos.x][pos.y].harry=myharry;
                if(map.map[pos.x][pos.y].isObject(Book.class)) map.map[pos.x][pos.y].deleteBook();
                if(map.map[pos.x][pos.y].isObject(Cloak.class)) map.map[pos.x][pos.y].deleteCloak();
                System.out.println("\n");
                map.printMap();
            }
        }


        static class BackTrackingPrecision2{
            Map grid;
            Harry harry;
            Map harryMap;
            boolean print = false;
            Stack<Position> path = new Stack<>();
            boolean[][] definedMap = new boolean[9][9];
            private static final int[] row = {-1, 0, 0, 1, -1, -1, 1, 1};
            private static final int[] col = {0, -1, 1, 0, -1, 1, 1, -1};
            private static final int[] rowPrecision = {-2, -2, -2, 2, 2, 2, -1, 0, 1, -1,0,1};
            private static final int[] colPrecision = {-1,0,1, -1,0,1,-2,-2,-2,2,2,2};
            private static final int[] rowFirst = {-2,0,0,2};
            private static final int[] colFirst ={0,-2,2,0};
            private static final int[] rowSecond ={-1,-1,1,1};
            private static final int[] colSecond ={-1,1,1,-1};
            public BackTrackingPrecision2(Map gameMap, Harry harry, boolean print){
                this.grid = new Map(gameMap);
                this.harry = new Harry(harry);
                harryMap = new Map();
                harryMap.setPlayer(this.harry, gameMap.findActor().x, gameMap.findActor().y);
                this.print=print;
            }



            public void checkPoss(int x, int y){
                for(int i=0;i<12; i++){
                    if(x+rowPrecision[i]>=0 && y+colPrecision[i]>=0 && x+rowPrecision[i]<grid.map.length && y+colPrecision[i]<grid.map[0].length){
                        harryMap.map[x+rowPrecision[i]][y+colPrecision[i]] = new Position(grid.map[x+rowPrecision[i]][y+colPrecision[i]]);
                        definedMap[x+rowPrecision[i]][y+colPrecision[i]] = true;
                    }
                }
            }

            public void checkFirst(int x, int y){
                checkPoss(x,y);
                definedMap[x][y]=true;
                for(int i=0;i<8;i++){
                    if(x+row[i]>=0 && y+col[i]>=0 && x+row[i]<grid.map.length && y+col[i]<grid.map[0].length){
                        if(i<4){
                            if(x+rowFirst[i]>=0 && y+colFirst[i]>=0 && x+rowFirst[i]<grid.map.length && y+colFirst[i]<grid.map[0].length){
                                definedMap[x+row[i]][y+col[i]] = !harryMap.map[x + rowFirst[i]][y + colFirst[i]].inspectorZone;
                            }
                        }else{
                            if(x+row[i]+rowSecond[i-4]>=0 && y+col[i]+colSecond[i-4]>=0 && x+row[i]+rowSecond[i-4]<grid.map.length && y+col[i]+colSecond[i-4]<grid.map[0].length){
                                if(!harryMap.map[x+row[i]+rowSecond[i-4]][y+col[i]].inspectorZone && !harryMap.map[x+row[i]][y+col[i]+colSecond[i-4]].inspectorZone){
                                    harryMap.map[x+row[i]][y+col[i]].inspectorZone = false;
                                    definedMap[x+row[i]][y+col[i]] = true;
                                }else{
                                    definedMap[x+row[i]][y+col[i]] = false;
                                }
                            }else{
                                harryMap.map[x+row[i]][y+col[i]].inspectorZone = false;
                                definedMap[x+row[i]][y+col[i]] = true;
                            }
                        }
                    }
                }
            }

            public boolean DFSUtil(int x, int y, boolean[][] visited){
                checkPoss(x,y);
                path.add(new Position(x,y));
                int H = grid.map.length;
                int L = grid.map[0].length;

                if(grid.map[x][y].isObject(Cloak.class)){
                    harry.haveCloak=true;
                    visited=new boolean[9][9];
                    grid.findCloak();
                    harryMap.findCloak();
                    grid.map[x][y].deleteCloak();
                    harryMap.map[x][y].deleteCloak();
                }

                if(grid.map[x][y].isObject(Book.class)){
                    visited=new boolean[9][9];
                    harry.haveBook=true;
                    grid.map[x][y].deleteBook();
                    harryMap.map[x][y].deleteBook();
                }

                if(grid.map[x][y].isObject(Exit.class) && harry.haveBook){
                    return true;
                }
                visited[x][y]=true;
                grid.map[x][y].harry=harry;
                harryMap.map[x][y].harry=harry;
                if(print)grid.printMap();
                if(print) System.out.println("\n");;

                for(int i=0;i<8;i++){
                    if(x+row[i]>=0 && y+col[i]>=0 && x+row[i]<H && y+col[i]<L){
                        if(definedMap[x+row[i]][y+col[i]]){
                            if(!harryMap.map[x+row[i]][y+col[i]].inspectorZone && !visited[x+row[i]][y+col[i]]){
                                grid.map[x][y].harry=null;
                                harryMap.map[x][y].harry=null;
                                if (DFSUtil(x+row[i],y+col[i], visited)) return true;
                                path.pop();
                            }
                        }
                    }

                }

                return false;
            }

            public boolean DFS(){
                Position harry = grid.findActor();
                int h = grid.map.length;
                int l = grid.map[0].length;
                checkFirst(harry.x, harry.y);
                boolean result = DFSUtil(harry.x, harry.y, new boolean[9][9]);

                return false;
            }

            public boolean HarryDeath(int x, int y){
                try {
                    return grid.map[x][y].harry != null && grid.map[x][y].inspectorZone;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    return false;
                }
            }
        }


        static class BackTracking {
            private Map grid;
            private Harry harry;
            public boolean printStep = false;
            private Stack<Position> path = new Stack<>();

            public BackTracking(Map gameMap) {
                this.grid = gameMap;
                this.harry = new Harry(gameMap.findActor().harry);
            }

            private boolean DFSUtil(int x, int y, boolean[][] visited) {
                if (HarryDeath(x, y)) {

                    return false;
                }
                int H = grid.map.length;
                int L = grid.map[0].length;
                path.push(new Position(x, y));
                if (x < 0 || y < 0 || x >= H || y >= L || visited[x][y] || grid.map[x][y].inspectorZone) {
                    return false;
                }

                if (grid.map[x][y].isObject(Cloak.class)) {
                    harry.haveCloak = true;
                    grid.findCloak();
                    grid.map[x][y].deleteCloak();
                    visited = new boolean[H][L];
                }
                if (grid.map[x][y].isObject(Book.class)) {
                    harry.haveBook = true;
                    grid.map[x][y].deleteBook();
                    return true;
                }
                if (grid.map[x][y].isObject(Exit.class) && harry.haveBook) {
                    return true;
                }

                grid.map[x][y].harry = harry;
                visited[x][y] = true;
                if (printStep) grid.printMap();
                if (printStep) System.out.println();
                grid.map[x][y].harry = null;
                if (DFSUtil(x + 1, y, visited)) return true;
                path.pop();
                if (DFSUtil(x - 1, y, visited)) return true;
                path.pop();
                if (DFSUtil(x, y + 1, visited)) return true;
                path.pop();
                if (DFSUtil(x, y - 1, visited)) return true;
                path.pop();
                if (DFSUtil(x + 1, y + 1, visited)) return true;
                path.pop();
                if (DFSUtil(x + 1, y - 1, visited)) return true;
                path.pop();
                if (DFSUtil(x - 1, y + 1, visited)) return true;
                path.pop();
                if (DFSUtil(x - 1, y - 1, visited)) return true;
                path.pop();
                return false;
            }

            public boolean DFS() {
                Position player = grid.findActor();
                int h = grid.map.length;
                int l = grid.map[player.x].length;
                if (printStep) System.out.println("DFS book:");
                boolean result = DFSUtil(player.x, player.y, new boolean[h][l]);
                if (!result) {
                    return false;
                }
                Stack<Position> partPath = new Stack<>();
                partPath.addAll(path);
                path.clear();
                if (printStep) System.out.println("\nDFS exit:");
                DFSUtil(partPath.peek().x, partPath.peek().y, new boolean[h][l]);
                partPath.pop();
                partPath.addAll(path);
                path.clear();
                path.addAll(partPath);
                return true;
            }


            private boolean HarryDeath(int x, int y) {
                try {
                    return grid.map[x][y].harry != null && grid.map[x][y].inspectorZone;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    return false;
                }

            }

            private void printStack(Stack<Position> pathPrint) {
                for (Position pos : pathPrint) {
                    System.out.print("[" + pos.x + ", " + pos.y + "] ");
                }
            }

            public void printStack() {
                printStack(path);
            }

        }

        static class Dijkstra {
            Map grid;
            Harry harry;
            private static final int[] row = {-1, 0, 0, 1, -1, -1, 1, 1};
            private static final int[] col = {0, -1, 1, 0, -1, 1, 1, -1};

            public Dijkstra(Map gameMap) {
                this.grid = gameMap;
                this.harry = new Harry(gameMap.findActor().harry);
            }


            private boolean isValid(Map gridNew, boolean[][] visited, int x, int y) {
                return x >= 0 && y >= 0 && x < gridNew.map.length && y < gridNew.map[0].length && !visited[x][y] && !gridNew.map[x][y].inspectorZone;
            }

            public Node DistanceFind(Map gridNew, int x, int y, EnvironmentObject object, int[][] disMatrix) {
                boolean[][] visited = new boolean[gridNew.map.length][gridNew.map[0].length];
                Queue<Node> queue = new ArrayDeque<>();
                int x_start = x;
                int y_start = y;
                visited[x_start][y_start] = true;
                queue.add(new Node(x_start, y_start, 0));
                disMatrix[x_start][y_start] = 0;
                int min = Integer.MAX_VALUE;
                Node minNode = new Node(queue.peek());

                if(HarryDeath(x,y)){
                    return null;
                }

                while (!queue.isEmpty()) {
                    Node node = queue.poll();
                    int x_step = node.x;
                    int y_step = node.y;
                    int dist = node.dist;

                    if (gridNew.map[x_step][y_step].isObject(object.getClass())) {
                        minNode = node;
                        min = dist;
                        break;
                    }
                    for (int k = 0; k < 8; k++) {
                        if (isValid(gridNew, visited, x_step + row[k], y_step + col[k])) {
                            visited[x_step + row[k]][y_step + col[k]] = true;
                            queue.add(new Node(x_step + row[k], y_step + col[k], dist + 1));
                            disMatrix[x_step + row[k]][y_step + col[k]] = dist + 1;
                        }
                    }

                }

                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (disMatrix[i][j] == 0) {
                            disMatrix[i][j] = Integer.MAX_VALUE;
                        }
                    }
                }
                disMatrix[x_start][y_start] = 0;

                if (min != Integer.MAX_VALUE) {
                    return minNode;
                }
                return null;
            }

            public Stack<Position> searchBookAndExit(Map gridNew) {
                int[][] distMatrix = new int[9][9];
                Node distance_Book = DistanceFind(gridNew, gridNew.findActor().x, gridNew.findActor().y, new Book(), distMatrix);
                if (distance_Book == null) {
                    return null;
                }


                Stack<Position> path = getPath(distMatrix, distance_Book);
                distMatrix = new int[9][9];
                Node distance_Exit = DistanceFind(gridNew, distance_Book.x, distance_Book.y, new Exit(), distMatrix);
                if(distance_Exit == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix,distance_Exit));
                return path;
            }

            public Stack<Position> searchCloakBookAndExit(Map gridNew) {
                int[][] distMatrix = new int[9][9];
                Node distance_Cloak = DistanceFind(gridNew, gridNew.findActor().x, gridNew.findActor().y, new Cloak(), distMatrix);
                if(distance_Cloak==null){
                    return null;
                }
                gridNew.findCloak();
                gridNew.map[distance_Cloak.x][distance_Cloak.y].deleteCloak();
                Stack<Position> path = getPath(distMatrix, distance_Cloak);

                distMatrix = new int[9][9];
                Node distance_Book = DistanceFind(gridNew, distance_Cloak.x, distance_Cloak.y, new Book(), distMatrix);
                if(distance_Book == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix, distance_Book));

                distMatrix = new int[9][9];
                Node distance_Exit = DistanceFind(gridNew, distance_Book.x, distance_Book.y, new Exit(), distMatrix);
                if(distance_Exit == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix, distance_Exit));
                return path;
            }

            public Stack<Position> searchBookCloakAndExit(Map gridNew) {

                int[][] distMatrix = new int[9][9];
                Node distance_Book = DistanceFind(gridNew, gridNew.findActor().x, gridNew.findActor().y, new Book(), distMatrix);
                if(distance_Book == null){
                    return null;
                }

                Stack<Position> path = getPath(distMatrix, distance_Book);

                distMatrix = new int[9][9];
                Node distance_Cloak = DistanceFind(gridNew, distance_Book.x, distance_Book.y, new Cloak(), distMatrix);
                if(distance_Cloak==null){
                    return null;
                }
                gridNew.findCloak();
                gridNew.map[distance_Cloak.x][distance_Cloak.y].deleteCloak();
                path.pop();
                path.addAll(getPath(distMatrix, distance_Cloak));
                distMatrix = new int[9][9];
                Node distance_Exit = DistanceFind(gridNew, distance_Cloak.x, distance_Cloak.y, new Exit(), distMatrix);
                if(distance_Exit == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix, distance_Exit));
                return path;
            }

            public Stack<Position> findMinDistance(){
                List<Stack<Position>> path = new ArrayList<>();
                path.add(searchBookAndExit(new Map(grid)));
                path.add(searchBookCloakAndExit(new Map(grid)));
                path.add(searchCloakBookAndExit(new Map(grid)));

                path.removeIf(Objects::isNull);
                if(path.isEmpty()) return null;
                int min = path.get(0).size();
                int index = 0;
                for (Stack<Position> way: path) {
                    if(way.size()<min){
                        min = way.size();
                        index = path.indexOf(way);
                    }
                }
                return path.get(index);
            }
            public Stack<Position> getPath(int[][] distMatrix, Node finalPos) {
                Stack<Position> path = new Stack<>();
                int x = finalPos.x;
                int y = finalPos.y;
                int min = Integer.MAX_VALUE;
                int x_next = 0;
                int y_next = 0;
                for (int i = 0; i < finalPos.dist; i++) {
                    path.add(new Position(x, y));
                    for (int k = 0; k < 8; k++) {
                        if(x+row[k]>=0 && y+col[k]>=0 && x+row[k]<distMatrix.length && y+col[k]<distMatrix[0].length){
                            if (distMatrix[x + row[k]][y + col[k]] < min) {
                                min = distMatrix[x + row[k]][y + col[k]];
                                x_next = x + row[k];
                                y_next = y + col[k];
                            }
                        }
                    }
                    x = x_next;
                    y = y_next;
                }
                path.add(new Position(x, y));
                Collections.reverse(path);
                return path;
            }

            private void printStack(Stack<Position> pathPrint) {
                for (Position pos : pathPrint) {
                    System.out.print("[" + pos.x + ", " + pos.y + "] ");
                }
            }

            private boolean HarryDeath(int x, int y) {
                try {
                    return grid.map[x][y].harry != null && grid.map[x][y].inspectorZone;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    return false;
                }

            }

        }


        static class DijkstraPrecision2{
            Map grid;
            Harry harry;
            Map harryMap;
            Stack<Position> path = new Stack<>();
            boolean[][] definedMap = new boolean[9][9];
            private static final int[] row = {-1, 0, 0, 1, -1, -1, 1, 1};
            private static final int[] col = {0, -1, 1, 0, -1, 1, 1, -1};
            private static final int[] rowPrecision = {-2, -2, -2, 2, 2, 2, -1, 0, 1, -1,0,1};
            private static final int[] colPrecision = {-1,0,1, -1,0,1,-2,-2,-2,2,2,2};
            private static final int[] rowFirst = {-2,0,0,2};
            private static final int[] colFirst ={0,-2,2,0};
            private static final int[] rowSecond ={-1,-1,1,1};
            private static final int[] colSecond ={-1,1,1,-1};
            public DijkstraPrecision2(Map gameMap, Harry harry){
                this.grid = new Map(gameMap);
                this.harry = new Harry(harry);
                harryMap=new Map();
                harryMap.setPlayer(this.harry,gameMap.findActor().x,gameMap.findActor().y);
            }

            public void checkPoss(int x, int y){
                for(int i=0;i<12; i++){
                    if(x+rowPrecision[i]>=0 && y+colPrecision[i]>=0 && x+rowPrecision[i]<grid.map.length && y+colPrecision[i]<grid.map[0].length){
                        harryMap.map[x+rowPrecision[i]][y+colPrecision[i]] = new Position(grid.map[x+rowPrecision[i]][y+colPrecision[i]]);
                        definedMap[x+rowPrecision[i]][y+colPrecision[i]] = true;
                    }
                }
            }

            public void checkFirst(int x, int y){
                checkPoss(x,y);
                definedMap[x][y]=true;
                for(int i=0;i<8;i++){
                    if(x+row[i]>=0 && y+col[i]>=0 && x+row[i]<grid.map.length && y+col[i]<grid.map[0].length){
                        if(i<4){
                            if(x+rowFirst[i]>=0 && y+colFirst[i]>=0 && x+rowFirst[i]<grid.map.length && y+colFirst[i]<grid.map[0].length){
                                definedMap[x+row[i]][y+col[i]] = !harryMap.map[x + rowFirst[i]][y + colFirst[i]].inspectorZone;
                            }
                        }else{
                            if(x+row[i]+rowSecond[i-4]>=0 && y+col[i]+colSecond[i-4]>=0 && x+row[i]+rowSecond[i-4]<grid.map.length && y+col[i]+colSecond[i-4]<grid.map[0].length){
                                if(!harryMap.map[x+row[i]+rowSecond[i-4]][y+col[i]].inspectorZone && !harryMap.map[x+row[i]][y+col[i]+colSecond[i-4]].inspectorZone){
                                    harryMap.map[x+row[i]][y+col[i]].inspectorZone = false;
                                    definedMap[x+row[i]][y+col[i]] = true;
                                }else{
                                    definedMap[x+row[i]][y+col[i]] = false;
                                }
                            }else{
                                harryMap.map[x+row[i]][y+col[i]].inspectorZone = false;
                                definedMap[x+row[i]][y+col[i]] = true;
                            }
                        }
                    }
                }
            }


            private boolean isValid(Map gridNew, boolean[][] visited, int x, int y) {
                return x >= 0 && y >= 0 && x < gridNew.map.length && y < gridNew.map[0].length && !visited[x][y] && !gridNew.map[x][y].inspectorZone && definedMap[x][y];
            }

            public Node DistanceFind(Map gridNew, int x, int y, EnvironmentObject object, int[][] disMatrix) {
                definedMap = new boolean[9][9];
                boolean[][] visited = new boolean[gridNew.map.length][gridNew.map[0].length];
                checkFirst(x,y);
                Queue<Node> queue = new ArrayDeque<>();
                int x_start = x;
                int y_start = y;
                visited[x_start][y_start] = true;
                queue.add(new Node(x_start, y_start, 0));
                disMatrix[x_start][y_start] = 0;
                int min = Integer.MAX_VALUE;
                Node minNode = new Node(queue.peek());

                if(HarryDeath(x,y)) return null;

                while (!queue.isEmpty()) {
                    Node node = queue.poll();
                    int x_step = node.x;
                    int y_step = node.y;
                    int dist = node.dist;
                    checkPoss(x_step,y_step);

                    if (gridNew.map[x_step][y_step].isObject(object.getClass())) {
                        minNode = node;
                        min = dist;
                        break;
                    }
                    for (int k = 0; k < 8; k++) {
                        if (isValid(gridNew, visited, x_step + row[k], y_step + col[k])) {
                            visited[x_step + row[k]][y_step + col[k]] = true;
                            queue.add(new Node(x_step + row[k], y_step + col[k], dist + 1));
                            disMatrix[x_step + row[k]][y_step + col[k]] = dist + 1;
                        }
                    }

                }

                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (disMatrix[i][j] == 0) {
                            disMatrix[i][j] = Integer.MAX_VALUE;
                        }
                    }
                }
                disMatrix[x_start][y_start] = 0;

                if (min != Integer.MAX_VALUE) {
                    return minNode;
                }
                return null;
            }

            private boolean HarryDeath(int x, int y) {
                try {
                    return grid.map[x][y].harry != null && grid.map[x][y].inspectorZone;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    return false;
                }

            }


            public Stack<Position> searchBookAndExit(Map gridNew) {
                int[][] distMatrix = new int[9][9];
                Node distance_Book = DistanceFind(gridNew, gridNew.findActor().x, gridNew.findActor().y, new Book(), distMatrix);
                if (distance_Book == null) {
                    return null;
                }


                Stack<Position> path = getPath(distMatrix, distance_Book);
                distMatrix = new int[9][9];
                Node distance_Exit = DistanceFind(gridNew, distance_Book.x, distance_Book.y, new Exit(), distMatrix);
                if(distance_Exit == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix,distance_Exit));
                return path;
            }

            public Stack<Position> searchCloakBookAndExit(Map gridNew) {
                int[][] distMatrix = new int[9][9];
                Node distance_Cloak = DistanceFind(gridNew, gridNew.findActor().x, gridNew.findActor().y, new Cloak(), distMatrix);
                if(distance_Cloak==null){
                    return null;
                }
                gridNew.findCloak();
                gridNew.map[distance_Cloak.x][distance_Cloak.y].deleteCloak();
                Stack<Position> path = getPath(distMatrix, distance_Cloak);

                distMatrix = new int[9][9];
                Node distance_Book = DistanceFind(gridNew, distance_Cloak.x, distance_Cloak.y, new Book(), distMatrix);
                if(distance_Book == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix, distance_Book));

                distMatrix = new int[9][9];
                Node distance_Exit = DistanceFind(gridNew, distance_Book.x, distance_Book.y, new Exit(), distMatrix);
                if(distance_Exit == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix, distance_Exit));
                return path;
            }

            public Stack<Position> searchBookCloakAndExit(Map gridNew) {

                int[][] distMatrix = new int[9][9];
                Node distance_Book = DistanceFind(gridNew, gridNew.findActor().x, gridNew.findActor().y, new Book(), distMatrix);
                if(distance_Book == null){
                    return null;
                }

                Stack<Position> path = getPath(distMatrix, distance_Book);

                distMatrix = new int[9][9];
                Node distance_Cloak = DistanceFind(gridNew, distance_Book.x, distance_Book.y, new Cloak(), distMatrix);
                if(distance_Cloak==null){
                    return null;
                }
                gridNew.findCloak();
                gridNew.map[distance_Cloak.x][distance_Cloak.y].deleteCloak();
                path.pop();
                path.addAll(getPath(distMatrix, distance_Cloak));
                distMatrix = new int[9][9];
                Node distance_Exit = DistanceFind(gridNew, distance_Cloak.x, distance_Cloak.y, new Exit(), distMatrix);
                if(distance_Exit == null){
                    return null;
                }
                path.pop();
                path.addAll(getPath(distMatrix, distance_Exit));
                return path;
            }

            public Stack<Position> getPath(int[][] distMatrix, Node finalPos) {
                Stack<Position> path = new Stack<>();
                int x = finalPos.x;
                int y = finalPos.y;
                int min = Integer.MAX_VALUE;
                int x_next = 0;
                int y_next = 0;
                for (int i = 0; i < finalPos.dist; i++) {
                    path.add(new Position(x, y));
                    for (int k = 0; k < 8; k++) {
                        if(x+row[k]>=0 && y+col[k]>=0 && x+row[k]<distMatrix.length && y+col[k]<distMatrix[0].length){
                            if (distMatrix[x + row[k]][y + col[k]] < min) {
                                min = distMatrix[x + row[k]][y + col[k]];
                                x_next = x + row[k];
                                y_next = y + col[k];
                            }
                        }
                    }
                    x = x_next;
                    y = y_next;
                }
                path.add(new Position(x, y));
                Collections.reverse(path);
                return path;
            }

            public Stack<Position> findMinDistance(){
                List<Stack<Position>> path = new ArrayList<>();
                path.add(searchBookAndExit(new Map(grid)));
                path.add(searchBookCloakAndExit(new Map(grid)));
                path.add(searchCloakBookAndExit(new Map(grid)));

                path.removeIf(Objects::isNull);
                if(path.isEmpty()) return null;
                int min = path.get(0).size();
                int index = 0;
                for (Stack<Position> way: path) {
                    if(way.size()<min){
                        min = way.size();
                        index = path.indexOf(way);
                    }
                }
                return path.get(index);
            }
        }

    }


        public static void main(String[] args) {
            //game.startBackTracking(false);
            //System.out.println("\n");
//            int success = 0;
//            long time = 0;
//            for(int i=0;i<1000;i++){
//                long timeStart = System.currentTimeMillis();
//                Game game = new Game(0);
//                if(game.startDijkstraStat()) {
//                    time+=System.currentTimeMillis()-timeStart;
//                    success++;
//                }
//            }
//            float timeStat = (float) time/success;
//            System.out.println(success+" "+timeStat);
             Game game = new Game();
             //game.startSecondPerceptionBackTracking(true);
            game.startBackTracking(false);

        }
}
