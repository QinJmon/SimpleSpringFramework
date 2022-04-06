package demo.annotation;

@CourseInfoAnnotation(courseName = "java",courseTag = "加油",courseProfile = "哈哈")
public class ImoocCourse {

    @PersonInfoAnnotation(name = "haha",language = {"java","c","python"})
    private  String author;

    @CourseInfoAnnotation(courseName = "c",courseTag = "ccc",courseProfile = "呵呵")
    public void getCourseInfo(){

    }
}
