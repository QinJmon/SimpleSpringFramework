package demo.pattern.factory.abstractf;

import demo.pattern.factory.entity.Keyboard;
import demo.pattern.factory.entity.Mouse;

public interface computerFactory {
    Mouse createMouse();
    Keyboard createKeyboard();
}
