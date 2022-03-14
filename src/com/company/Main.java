package com.company;

import java.util.Random;
import java.util.Scanner;

public class Main {

    static class Position {
        public int x;
        public int y;
        public boolean dangerous;
        public Position(int x, int y){
            this.x = x;
            this.y = y;
            this.dangerous = false;
        }

        public boolean checkPosition(int x, int y){
            if(this.x==x && this.y == y) return true;
            return false;
        }
    }

    static class AgentEnemy{
        public Position position;
        public int zone;
    }

    static class ArgusFilch extends AgentEnemy{
        public ArgusFilch(Position position){
            this.position = position;
            this.zone = 2;
        }
    }

    static class MrsNorris extends AgentEnemy{
        public MrsNorris(Position position){
            this.position = position;
            this.zone = 1;
        }
    }

    static class AgentFriendly{
        public Position position;
        public AgentFriendly(Position position){
            this.position = position;
        }
    }

    static class Book extends AgentFriendly{
        public Book(Position position){
            super(position);
        }
    }

    static class Exit extends AgentFriendly{
        public Exit(Position position){
            super(position);
        }
    }

    static class Cloak extends AgentFriendly{
        public Cloak(Position position){
            super(position);
        }
    }

    static class Harry{
        public Position position;
        public int zone;
        public Harry(Position position){
            this.position = position;
            zone = 1;
        }

        public Position go(Position[] possibles){
            Random random = new Random();
            Position myNew = possibles[random.nextInt(possibles.length)];
            this.position = myNew;
            return myNew;
        }

    }

    static class Map{
        private final int size = 9;
        private char[][] map;
        public Map(){
            this.map = new char[9][9];
            for(int i=0;i<9;i++){
                for(int k=0;k<9;k++){
                    map[i][k]='*';
                }
            }
        }

        public void setEnemy(AgentEnemy enemy){
            Position position = enemy.position;

            for(int i = 0; i<9;i++){
                for(int k = 0; k<9;k++){
                    if(Math.abs(position.x-i)<=enemy.zone && Math.abs(position.y-k)<=enemy.zone){
                        map[i][k] = 'D';
                    }
                }
            }
            map[position.x][position.y] = 'C';
        }
        public void setFriend(AgentFriendly friend){
            map[friend.position.x][friend.position.y]='F';
        }

        public void setPlayer(Harry harry){
            map[harry.position.x][harry.position.y]='H';
        }

        public void printMap(){
            for(int i=0;i<9;i++){
                for(int k=0;k<9;k++){
                    System.out.print(map[i][k]+" ");
                }
                System.out.println();
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
        private Map gameMap;
        private Map harryMap;
        public Game(){
            harry = new Harry(new Position(8,0));
            book = new Book(new Position(4,7));
            exit = new Exit(new Position(4,1));
            argusFilch = new ArgusFilch(new Position(6,4));
            mrsNorris = new MrsNorris(new Position(1,2));
            cloak = new Cloak(new Position(0,0));

            gameMap = new Map();

            gameMap.setEnemy(argusFilch);
            gameMap.setEnemy(mrsNorris);
            gameMap.setFriend(book);
            gameMap.setFriend(cloak);
            gameMap.setFriend(exit);
            gameMap.setPlayer(harry);
            gameMap.printMap();
        }
    }








    public static void main(String[] args) {
    Game game = new Game();
    }
}
