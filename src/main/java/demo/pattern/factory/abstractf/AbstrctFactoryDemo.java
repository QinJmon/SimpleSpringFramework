package demo.pattern.factory.abstractf;

import demo.pattern.factory.entity.Keyboard;
import demo.pattern.factory.entity.Mouse;

public class AbstrctFactoryDemo {
    public static void main(String[] args) {
        computerFactory cf=new HpComputer();
        Keyboard keyboard = cf.createKeyboard();
        Mouse mouse = cf.createMouse();
        mouse.sayHi();
        keyboard.sayHello();

    }
}
