import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class YKCube extends PApplet {

PFont Font1;


class CubeButton {
  java.awt.Polygon p = new java.awt.Polygon();
  ArrayList<CubeButton> connected = new ArrayList<CubeButton>(); 
  ArrayList<CubeButton> locks = new ArrayList<CubeButton>(); 
  float x, y, size = 0;
  int def_color = color(230,230,230);
  int select_color = color(80,80,220);
  int correct_color = color(80,180,80);
  int wrong_color = color(180,80,80);
  int button_color = select_color;
  int pressLevel = 0;
  boolean first = false;
  int result = 0;
  
  String ImageString = "darius.jpg";
  PImage Image = loadImage(ImageString);
  String Category = "League of legends";
  String Question = "Darius nereli?\n a) Damecia\n b) Noxus\n c) Ahameniş\n d) Diyarbakır";
  String Answer = "b) Noxus";
  int Point = 100;
  
  CubeButton(float x, float y, float size, String fileName, String category, int point) {
    this.Point = point;
    this.Category = category;
    this.ImageString = "sorular_resim/"+fileName+".jpg";
    this.Image = loadImage(ImageString);
    
    String[] sorular = loadStrings("sorular_yazı/"+fileName+".txt");
    String[] cevaplar = loadStrings("cevaplar_yazı/"+fileName+".txt");
    
    this.Question = "";
    for (int i = 0 ; i < sorular.length; i++) {
      this.Question += sorular[i] + "\n";
    }

    this.Answer = "";
    for (int i = 0 ; i < cevaplar.length; i++) {
      this.Answer += cevaplar[i] + "\n";
    }
    
    this.x = x;
    this.y = y;
    this.size = size;
    this.button_color = def_color;
  }
  
  public void textConf(int align, int size, int c, boolean symbol) {
    if(symbol) textFont(createFont("Roman", 64, true));
    else textFont(Font1);
    textAlign(align);
    textSize(size);
    fill(c);
  }
  
  public void show() {
    pushMatrix();
    translate(x,y);
    fill(button_color);
    //rotate(frameCount / 50.0);
    strokeWeight(3);
    polygon(0, 0, size, 4);  // Icosagon
    // 2k - yatay 25
    // 2k - dikey 36
    textConf(CENTER,16,def_color,false); //
    text(Category, -20-size/2, 20-size/2, size+40, size*2);
    popMatrix();
  }
  
  public void showQuestion() {
    image(Image, 100,25,width-200, height/2);
    textConf(CENTER,48,def_color,false);
    text(Question, 0, height/2+30, width, height/2);
  }
  
  public void showAnswer() {
    
    textConf(CENTER,64,def_color,false);
    text(Answer, 0, height/2, width, height);
    
    fill(wrong_color);
    rect(100, height/2-50, 200, 100);
    
    textConf(CENTER,64,def_color,true);    
    text("❌", 100, height/2-50, 200, 100);
    
    fill(correct_color);
    rect(width-250, height/2-50, 200, 100);
    
    textConf(CENTER,64,def_color,true);    
    text("✔", width-250, height/2-50, 200, 100);
  }
  
  public boolean check_locks() {
    boolean not_locked = first;
    for(CubeButton lock: locks) {
      if(lock.result==1) not_locked = true;
    }
    return not_locked;
  }
  
  public boolean inside(int mx, int my) {
    //return p.contains(mx, my);
    float dist = PVector.dist(new PVector(this.x, this.y), new PVector(mx,my));
    return (dist < size/2) && check_locks();
  }
  
  public void polygon(float x, float y, float radius, int npoints) {
    float angle = TWO_PI / npoints;
    beginShape();
    for (float a = 0; a < TWO_PI; a += angle) {
      float sx = x + cos(a) * radius;
      float sy = y + sin(a) * radius;
      vertex(sx, sy);
      p.addPoint((int)sx,(int)sy);
    }
    endShape(CLOSE);
  }
  
  public int pressed(int mode) {
    if (mode == 0) {
      
      if(pressLevel == 0) {
        button_color = select_color;
      } else if(pressLevel == 1) {
        screenMode = 1;
        selected = this;
        Category = Point+ " Puan";
      }
      pressLevel++;
      return 0;
      
    } else if (mode == 2) {
      boolean correct = (mouseX > width-250) && (mouseY > height/2-50) && (mouseX < width-50) && (mouseY < height/2+50);
      if(correct) {
        result = 1;
        return 1;
      }
      boolean wrong = (mouseX > 100) && (mouseY > height/2-50) && (mouseX < 300) && (mouseY < height/2+50);
      if(wrong) {
        result = 2;
        return 2;
      }
    }
    return -1;
    
  }

  
}
Table table;

CubeButton selected;
ArrayList<CubeButton> buttons = new ArrayList<CubeButton>();
int max = 4;
int[] cubes = {1,2,3,2,3,4,3,2,3,2,1};
public void setup() {
  table = loadTable("info.csv", "header");
  Font1 = createFont("Arial Bold", 18);
  textFont(Font1);
  //size(1600, 1200);
  
  print(height);
  
  float cube_size = 80f; // 110f
  
  ArrayList<String> files = new ArrayList<String>();
  ArrayList<String> cats = new ArrayList<String>();
  ArrayList<Integer> ids = new ArrayList<Integer>();
  ArrayList<Integer> points = new ArrayList<Integer>();
  
  for (TableRow row : table.rows()) {
    
    files.add(row.getString("file"));
    cats.add(row.getString("cat"));
    ids.add(row.getInt("id"));
    points.add(row.getInt("point"));
  }
  
  int k = 25;
  for(int j = 0; j < cubes.length; j++) {
    int cube_count = cubes[j];
    float offset = width/4-(cube_count-1)*cube_size;
    //float offset = (j%2==0)? 40f:0f;
    for(int i = 0; i < cube_count; i++) {
      
      buttons.add(new CubeButton(offset + 120 + i * cube_size*2,  160 + j * cube_size, cube_size, files.get(k), cats.get(k), points.get(k)));
      
      k--;
    }
  }
  
  for(int i = 0; i < buttons.size(); i++) {
    for(int j = 0; j < buttons.size(); j++) {
      if(i==j) continue;
      CubeButton from = buttons.get(i);
      CubeButton to = buttons.get(j);
      if(from.y >= to.y) continue;
      if(PVector.dist(new PVector(from.x, from.y), new PVector(to.x, to.y)) > (from.size+to.size)) continue;
      from.locks.add(to);
    }
  }
  
  for(int i = 0; i < buttons.size(); i++) {
    for(int j = 0; j < buttons.size(); j++) {
      if(i==j) continue;
      CubeButton from = buttons.get(i);
      CubeButton to = buttons.get(j);
      if(from.y <= to.y) continue;
      if(PVector.dist(new PVector(from.x, from.y), new PVector(to.x, to.y)) > (from.size+to.size)) continue;
      from.connected.add(to);
    }
  }
  
  buttons.get(buttons.size()-1).pressed(0);  
  buttons.get(buttons.size()-1).first = true;
}

String[] record = new String[25];

int screenMode = 0;
public void draw() {
  background(20);
  
  if (screenMode == 0) {
    textSize(64);
    text("YKCube", width/2-140, 20, 280, 240);
    for(CubeButton btn: buttons) {
      btn.show();
    }
    fill(250);
    //rect(width/2+120, 60, width/2-200, height-120);
  } else if (screenMode == 1) {
    selected.showQuestion();
  } else if(screenMode == 2) {
    selected.showAnswer();
  }
  
  
}

public void mousePressed() {
  
  if (screenMode == 0) {
    for(CubeButton btn: buttons) {
      if(btn.inside(mouseX,mouseY)) {
        btn.pressed(screenMode);
      }
    }
  } else if (screenMode == 1) {
    screenMode ++;
  } else if(screenMode == 2) {
    int returned = selected.pressed(screenMode);
    if (returned == 1) selected.button_color = selected.correct_color;
    else if (returned == 2) selected.button_color = selected.wrong_color;
    else return;
    screenMode = 0;
    selected = null;
  }
  
  
  
}
  public void settings() {  fullScreen(1); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "YKCube" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
