package demo.pattern.eventmode;

public class EventModeDemo {
    public static void main(String[] args) {
        Event event=new Event();
        EventSource eventSource=new EventSource();
        SingleClickEventListener singleClickEventListener=new SingleClickEventListener();
        DoubleClickEventListener doubleClickEventListener=new DoubleClickEventListener();
        eventSource.register(singleClickEventListener);//将监听器注册到事件源中
        event.setType("singleclick");//设置事件的属性
        eventSource.publishEvent(event);//将事件发布
    }
}
