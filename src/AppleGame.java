//ralph perricelli
import java.awt.Color;
import java.util.Random;
import tester.*;
import javalib.funworld.*;
import javalib.colors.*;
import javalib.worldcanvas.*;
import javalib.worldimages.*;

//to represent a list of apples
interface ILoApple {
    //get first in a list
    Apple getFirst();
    //get rest in a list
    ILoApple getRest();
    //move apples in a list
    ILoApple fall();
    //draw apples
    WorldImage drawList();
    //any apples caught
    boolean anyCaught(Basket b);
    
}

//to reperesent an empty
class MtLoApple implements ILoApple {
    //constructor
    MtLoApple(){
        //nothing because its supposed to be empty
    }

    //get first 
    public Apple getFirst() {
        return new Apple(new Posn(0,0), 0 , new White());
    }

    //get rest
    public ILoApple getRest() {
        // TODO Auto-generated method stub
        return new MtLoApple();
    }

    //move apples in a list
    public ILoApple fall() {
        // TODO Auto-generated method stub
        return new MtLoApple();
    }

    public WorldImage drawList() {
        return new CircleImage(new Posn(0,0), 0, new White());
    }

    //is an apple caught
    public boolean caughtApple(Basket b) {
        // TODO Auto-generated method stub
        return false;
    }

    //any caught
    public boolean anyCaught(Basket b) {
        // TODO Auto-generated method stub
        return false;
    }
}

//to represent a cons lo apple
class ConsLoApple implements ILoApple {
    Apple first;
    ILoApple rest;
    //constructor
    ConsLoApple(Apple first, ILoApple rest) {
        this.first = first;
        this.rest = rest;
    }
    
    //get first
    public Apple getFirst() {
        // TODO Auto-generated method stub
        return first;
    }
    //get rest
    public ILoApple getRest() {
        // TODO Auto-generated method stub
        return rest;
    }

    //move apples in a list
    public ILoApple fall() {
        // TODO Auto-generated method stub
        return new ConsLoApple(this.getFirst().fall(), this.getRest().fall());
    }

    //draw a list of apples
    public WorldImage drawList() {
        // TODO Auto-generated method stub
        return this.getFirst().appleImage().overlayImages(
                this.getRest().drawList());
    }
    
  //determine if any apples in a list are in the basket
    public boolean anyCaught(Basket b) {
       return this.first.inBasket(b) ||
               this.rest.anyCaught(b);
    }
}
//-----------------------------Apple Class--------------------------------//
//to represent an apple
class Apple {
    Posn center;
    int radius;
    IColor col;
    
    //constructor
    Apple(Posn center, int radius, IColor col) {
        this.center = center;
        this.radius = radius;
        this.col = col;
    }

//-----------------------Functions/methods that involve gameplay-----------------//
    //move the apple downward at a fixed rate
    public Apple moveDown() {
        return new Apple(new Posn(
                this.center.x, 
                this.center.y + 25), 
                this.radius, 
                this.col);
    }
    
    //check if apple is on the ground
    public boolean onTheGround() {
        if (this.center.y + this.radius >= 400) {
            return true;
        }
        else {
            return false;
        }
    }
    
    //determine new apple
    public Apple fall() {
        if (this.onTheGround()) {
            return new Apple(
                    new Posn(randomVal(0, 400), randomVal(0, 15)), 
                    this.radius, 
                    this.col);
        }
        else return this.moveDown();
    }
    
    //random apple location(for use when we catch an apple)
    public Apple randomLoc() {
        return new Apple(new Posn(randomVal(0, 400), 
                this.radius + 5), 
                this.radius, 
                this.col);
    }
    
    //get a random number between two values
    int randomVal(int min, int max) {
        return (int) (Math.floor(Math.random() * (max - min)) + min);
    }
    
    //is a given apple in a basket?
    public boolean inBasket(Basket b) {
        return this.center.y <= b.center.y + (b.size / 2) &&
                this.center.y >= b.center.y - (b.size / 2) &&
                this.center.x >= b.center.x - (b.size / 2) &&
                this.center.x <= b.center.x + (b.size / 2);
    }
//---------------------------Drawing Methods for apples-----------------------------------//
// produce the image of this apple at its current location and color
    WorldImage appleImage() {
        return new CircleImage(this.center, this.radius, this.col).overlayImages(
                new FromFileImage(this.center, "red-apple.png"));
    }
    
}
//---------------------------------End Apple Class------------------------------------------//


//---------------------------------Basket Class--------------------------------------------//
//to represent a basket
class Basket {
    Posn center;
    int size; //im choosing to use a square so im only using side length
    IColor col;
    
    //constructor
    Basket(Posn center, int size, IColor col) {
        this.center = center;
        this.size = size;
        this.col = col;
    }
//-----------------------Methods that involve gameplay-----------------//    
    //moveOnKey(String ke)
    public Basket moveOnKey(String ke) {
        if (ke.equals("left")) {
            return new Basket(new Posn(
                    this.center.x - 10,
                    this.center.y), 
                    this.size, 
                    this.col);
        }
        else if (ke.equals("right")) {
            return new Basket(new Posn(
                    this.center.x + 10,
                    this.center.y), 
                    this.size, 
                    this.col);
        }
        else {
            return new Basket(this.center, this.size, this.col);
        }
    }
    
//--------------------------Drawing Basket----------------------------------//
// produce the image of this blob at its current location and color */
    WorldImage basketImage() {
        return new RectangleImage(this.center, this.size, this.size, this.col).overlayImages(
                new FromFileImage(this.center, "basket.png"));
    }
}
//-------------------------End basket Class----------------------------------//



//to represent a "game"/ world enviroment
class AppleGame extends World {
    int width = 400;
    int height = 400;
    int numCaught = 0;
    ILoApple a; //initial apple/changed to list of apples
    Basket b; //initial basket
    
    //constructor
    public AppleGame(ILoApple a, Basket b, int numCaught) {
        super();
        this.a = a;
        this.b = b;
        this.numCaught = numCaught;
    }
//-----------------------Methods that involve gameplay-----------------//    
  //move basket on key
    public World onKeyEvent(String ke) {
        if (ke.equals("left")) {
            return new AppleGame(this.a, this.b.moveOnKey(ke), this.numCaught);
        }
        else if (ke.equals("right")) {
            return new AppleGame(this.a, this.b.moveOnKey(ke), this.numCaught);
        }
        else return new AppleGame(this.a, this.b, this.numCaught);
    }

    //tick method 
    public World onTick() {
        if (a.anyCaught(b)) {
            return new AppleGame(a.fall(), b, (this.numCaught + 1));
        }
        return new AppleGame(a.fall(), b, this.numCaught);
    }
    
//--------------------------Drawing Our World / AppleGame----------------------------------//    
    //background image for our game
    public WorldImage treeImage =
        new FromFileImage(new Posn(200, 200), "apple-tree.png");
    
    //helper function to display the score
    String convert(int x) {
        return Integer.toString(x);
    }
    
    //Show the score on the screen
    public WorldImage scoreImage(int score) {
            return new TextImage(new Posn(370, 35), convert(score), new Black());
    }
    
     //produce the image of this world by adding images to the background image
    public WorldImage makeImage() {
        return new OverlayImages(this.treeImage, new OverlayImages(
                this.a.drawList(), new OverlayImages(scoreImage(numCaught), this.b.basketImage())));
    }

    
    //produce the last image of this world
    public WorldImage lastImage(String s) {
        return new OverlayImages(this.makeImage(), new TextImage(new Posn(200, 200), s, Color.black));
    }
    
    //what to do when the game is over
    public WorldEnd worldEnds() {
        // if they caught the correct number of apples end the game
        if (this.numCaught == 10) {
            return new WorldEnd(true, new OverlayImages(this.makeImage(), 
                new TextImage(new Posn(200, 200), "You WIN! Take ur Apples & and go plz", Color.black)));
        }
        else {
            return new WorldEnd(false, this.makeImage());
        }
    }
}

//Examples Class/Testing
class AppleGameExamples {
   
    public static void main(String[] args){
    //examples of apples for our apple class
    Apple a1 = new Apple(new Posn(200, 20), 15, new Red());
    Apple a2 = new Apple(new Posn(150, 20), 15, new Yellow());
    Apple a3 = new Apple(new Posn(250, 20), 15, new Blue());
    Apple a4 = new Apple(new Posn(300, 20), 15, new Green());
    
    //list of apples
    ILoApple l1 = new ConsLoApple(a1, new ConsLoApple( a2,
            new ConsLoApple(a3, new ConsLoApple(a4,
                    new MtLoApple()))));
    
    //examples of a basket for our game
    Basket b = new Basket(new Posn(200, 370), 40, new White());
    
    //examples of data for AppleGame class
    AppleGame ag1 = new AppleGame(l1, b, 0);
    //AppleGame ag2 = new AppleGame(a2, b, 0);
    
 // run the first game
    ag1.bigBang(400, 400, 0.3);
    }
}