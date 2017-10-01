/*

 The class inherit all the fields, constructors and functions 
 
 of the java.awt.Polygon class, including contains(), xpoint,ypoint,npoint
 
 */

class Poly extends java.awt.Polygon {


  public Poly(int[] x, int[] y, int n) {

    //call the java.awt.Polygon constructor
    super(x, y, n);
  }

  void valorXY(int a, int b, int index) {
    xpoints[index]=a;
    ypoints[index]=b;
  }

  void drawMe() {

    pushStyle();
    noFill();
    stroke(0, 0, 255);
    beginShape();

    for (int i = 0; i < npoints; i++) {

      vertex(xpoints[i], ypoints[i]);
    }

    endShape(CLOSE);
    popStyle();
  }
  
    void drawMe2() {
    pushStyle();
    noFill();
    stroke(0, 0, 255);
    beginShape();

    for (int i = 0; i < npoints; i++) {

      vertex(xpoints[i]+640, ypoints[i]);
    }

    endShape(CLOSE);
    popStyle();
  }
  
  int polyArea(){ //retorna o valor em numero de pixels
  int area = 0;
  int j = npoints-1; //o último vertice é o anterior do primeiro
  for(int i = 0; i<npoints; i++){
  area = area + (xpoints[j]+xpoints[i]) * (ypoints[j]-ypoints[i]);
  j = i; //j é o vertice anterior ao i
  }
  area = area/2;
  return area;
  }

}