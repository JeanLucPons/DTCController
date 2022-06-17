package DTCController;

public class CarItem {

  CarItem(String name,String software,String ecuRef) {
    this.name = name;
    this.software = software;
    this.ecuRef = ecuRef;
  }
  String name;
  String software;
  String ecuRef;

  @Override
  public String toString() {
    return software + ":" + name + " (" + ecuRef + ")";
  }

}
