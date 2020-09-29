import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class  MainLesson1 {
    public static void main(String[] args) {
        //Первое задание
        ObjectManager<String> strObj = new ObjectManager<String>("1", "2", "3", "4", "5");
        System.out.println(Arrays.toString(strObj.getObjects()));
        strObj.switcher(0, 4);
        System.out.println(Arrays.toString(strObj.getObjects()));
        //Второе задание
        List<String> list = new ArrayList<String>();
        list = strObj.massToArrayList();
        for (String str : list)
            System.out.print(" " + str);
        System.out.println();
        strObj.switcher(0, 4);
        for (String str : list)
            System.out.print(" " + str);
        System.out.println();
        //Третье задание
        Box<Apple> appleBox = new Box<Apple>(new Apple(),new Apple(), new Apple());
        Box<Orange> orangeBox = new Box<Orange>(new Orange(), new Orange());
        System.out.println("Коробка с яблоками весит: "+appleBox.getWeight());
        System.out.println("Коробка с апельсинами весит: "+orangeBox.getWeight());
        System.out.println("Вес коробки с яблоками равен весу коробки с апельсинами? "+appleBox.compare(orangeBox));
        orangeBox.addFruit(new Orange());
        System.out.println("Вес коробки с яблоками равен весу коробки с апельсинами? "+appleBox.compare(orangeBox));

    }

}

class ObjectManager<T extends Object> {
    private T[] objects;

    public ObjectManager(T... objects) {
        this.objects = objects;
    }

    public void switcher(int firstId, int secondId) {
        T temp;
        temp = objects[firstId];
        objects[firstId] = objects[secondId];
        objects[secondId] = temp;
    }

    public T[] getObjects() {
        return objects;
    }

    public List<T> massToArrayList(){
        List<T> listOfObjects = new ArrayList<T>();
        listOfObjects = Arrays.asList(objects);
        return listOfObjects;
    }
}

abstract class Fruit{
    public double getWeight() {
        return weight;
    }

    private double weight;

   public Fruit() {
   }

}

class Apple extends Fruit{
    public double getWeight() {
        return weight;
    }

    private double weight = 1;

    public Apple() {
    }
}

class Orange extends Fruit{
    public double getWeight() {
        return weight;
    }

    private double weight = 1.5;

    public Orange(){
    }
}

class Box<F extends Fruit>{
    private List<F> listOfFruits;


    public Box(F... fruits){
    listOfFruits = new ArrayList<F>(Arrays.asList(fruits));
    }

    public double getWeight(){
        double weight = 0.0;
        for(F f: listOfFruits){
           weight += f.getWeight();
        }
        return weight;
    }

    public boolean compare(Box b){
        if(b.getWeight() == this.getWeight()){
            return true;
        }
        return false;
    }

    public void addFruit(F fruit){
        listOfFruits.add(fruit);
    }

}

