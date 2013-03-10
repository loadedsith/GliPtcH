	import controlP5.*;
	import java.io.*;
	import javax.imageio.*;
	import java.util.Random;
	import java.awt.image.BufferedImage;
	import processing.core.*;

public class GliPtch extends PApplet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3978460229556397965L;
	// Open a file and read its binary data 
	public byte b[];
	public PImage img;
	public byte bGlitched[];
	public ControlP5 cp5;
	public String urlValue = "2.jpg";
	public Textfield imageUrlTextField;
	public Bang defaultImageButton;
	public Bang glitchButton;
	public Toggle glitchLoopToggle;
	public Toggle saveImageLoopToggle;
	public Bang pasteButton;
	public Bang saveImageButton;
	public int padding;
	public int leftBumper;
	public int rowHeight;
	public int rowBumper;
	public boolean glitchLoop;
	public boolean saveFramesLoop;
	public int headerOffset;
	public int damageBitEvery;
	public Slider headerOffsetSlider;
	public Slider damageBitEverySlider;
	public Slider damageChunkSlider;
	public int cp5Color;
	public String statusMessage;
	public int damageChunk;
	public Random rand;
	public String currentImageName;
	public ByteArrayInputStream bis;
	public BufferedImage bimg;
	void plotMemUsage(long report) {
	  stroke(0, 255, 0);
	  long allocated = Runtime.getRuntime().totalMemory();
	  rect(20, height-100-((allocated/report)*25), 2, 2);


	  stroke(255, 0, 0);

	  rect(20, height-100, 2, 2);
	  noStroke();
	}
	public static void main(String args[]) {
		    PApplet.main(new String[] {  "GliPtch" });
		    
		  }
	public void setup() {
	  rand = new Random();
	  currentImageName ="";
	  glitchLoop = false;
	  saveFramesLoop = false;
	  padding =3;
	  headerOffset=64;
	  damageBitEvery=1100;
	  statusMessage= "Welcome to GPictH";
	  size(1000, 786);
	  frameRate(30);
      PFont font = createFont("arial", 13);
	  //noLoop();
	  background(0);
	 // b = loadBytes(urlValue);
	  //println(b[0]);
	 // bGlitched = loadBytes(urlValue); 
	//  img = loadImage(urlValue);

	  cp5 = new ControlP5(this);
	  //img.loadPixels();
	  // Print each value, from 0 to 255 
	  generateNewGlitch();
	  imageUrlTextField = cp5.addTextfield("imageurl")
	    .setSize(160, 28)
	      .setFont(font)
	        .setText(urlValue)
	          .setFocus(false)
	            .setColor(color(255, 0, 0))
	              ;
	  defaultImageButton = cp5.addBang("defaultImage");
	  defaultImageButton.setSize(5*13+padding, 20)
	    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
	      ; 
	  glitchButton = cp5.addBang("glitch");
	  glitchButton.setSize(5*7+padding, 20)
	    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
	      ;
	  glitchLoopToggle = cp5.addToggle("play");
	  glitchLoopToggle.setSize(5*8+padding, 20)
	    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
	      ;
	  pasteButton = cp5.addBang("pasteurl");
	  pasteButton.setSize(5*8+padding, 20)
	    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
	      ;  
	  saveImageButton = cp5.addBang("saveImage");
	  saveImageButton.setPosition(20, 90)
	    .setSize(5*10+padding, 20)
	      .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
	        ;
	  saveImageLoopToggle = cp5.addToggle("saveframes");
	  saveImageLoopToggle.setSize(5*12+padding, 20)
	    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
	      ;
	  headerOffsetSlider = cp5.addSlider("headerOffset")
	    .setPosition(64, 50)
	      .setSize(100, 20)
	        .setRange(0, 2048)
	          .setValue(64)
	            ;
	  damageChunkSlider = cp5.addSlider("damageChunk")
	    .setPosition(100, 50)
	      .setSize(100, 20)
	        .setRange(0, 64)
	          .setValue(10)
	            ;
	  damageBitEverySlider = cp5.addSlider("damageBitEvery")
	    .setPosition(100, 50)
	      .setSize(width-140, 20)
	        .setRange(0, 4096);
	  damageBitEverySlider.setValue(1100)
	    ;

	  textFont(font);
	  // Print a blank line at the end 
	  tryAGlitch();
	  cp5Color = cp5.getController("saveframes").getColor().getBackground();
	  setLock(cp5.getController("saveframes"), true);

	  //img.updatePixels();
	}
	void configureUI() {
	  leftBumper = 10;
	  rowHeight = 33;
	  rowBumper = rowHeight + padding;
	  damageBitEverySlider.setPosition(leftBumper, rowBumper*1-rowHeight);
	  damageBitEverySlider.setRange(0, b.length);
	  glitchButton.setPosition(leftBumper, rowBumper*4-rowHeight);
	  glitchLoopToggle.setPosition(leftBumper+70, rowBumper*4-rowHeight);
	  pasteButton.setPosition(leftBumper, rowBumper*6-rowHeight);
	  saveImageButton.setPosition(leftBumper, rowBumper*5-rowHeight);
	  saveImageLoopToggle.setPosition(leftBumper+70, rowBumper*5-rowHeight);
	  defaultImageButton.setPosition(leftBumper+70, rowBumper*6-rowHeight);
	  headerOffsetSlider.setPosition(leftBumper, rowBumper*2-rowHeight);
	  headerOffsetSlider.setRange(0, b.length/16+64);
	  damageChunkSlider.setPosition(leftBumper, rowBumper*3-rowHeight);
	  imageUrlTextField.setPosition(leftBumper, rowBumper*7-rowHeight-7);
	}
	void generateNewGlitch() {
	  byte[] damage = new byte[damageChunk];
	  try {
	    for (int i = b.length-1; i > 0; i--) 
	    { 
	      bGlitched[i]= b[i];
	      if ((i % damageBitEvery) == 0 && i>headerOffset) {

	        rand.nextBytes(damage);
	        //   println("asdf"+howManyBytes);
	        for (int ii= damage.length-1; ii>0; ii--) {
	          bGlitched[i+ii]=damage[ii];
	        }

	        //   bGlitched[i]=b[(int)random(b.length-1)];
	      }
	    }
	    img = getAsImage(bGlitched);
	    //statusMessage = "Glitch Generated";
	  }
	  catch(Exception e) {
	    stroke(255, 000, 000);
	    statusMessage = "Bad Stream, try again"+e.getMessage();
	    stroke(0);
	  }
	 
	}
	public PImage getAsImage(byte[] bytes) {
	  try {
		   // ByteArrayInputStream bis=new ByteArrayInputStream(bytes); 
		    //BufferedImage bimg = ImageIO.read(bis); 

	    bis = new ByteArrayInputStream(bytes);
	    
	    bimg = ImageIO.read(bis); 
	    img=createImage(bimg.getWidth(), bimg.getHeight(), PConstants.ARGB);
	    bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
	    // img.updatePixels();
	    //println("getAsImage: FreeMem: "+Runtime.getRuntime().freeMemory());

	    return img;
	  }
	  catch(Exception e) {
	    statusMessage = "Can't create image from buffer"+e.getMessage();
	    println("Can't create image from buffer"+e.getMessage());
	    // e.printStackTrace();
	  }
	  
	  return null;
	}

	public void draw() {
	  background(0);
	  if (glitchLoop == true) {
	    tryAGlitch();
	  
	  }
	 
	  try {
	    image(img, 180, 30);
	    g.removeCache(img);
	  }
	  catch(Exception e) {

	    //statusMessage = "Image broke"+e.getMessage();
	  }
	  text(statusMessage, 20, height-20);
	}
	public void saveImage() {
	  String path = dataPath("Images/");

	  File file = new File(path);
	  int sizeOfFolder = 0;

	  if (file.exists() && file.isDirectory()) {
	    sizeOfFolder = file.listFiles().length;
	  }
	  try {
	    img.save((dataPath("images/" + (sizeOfFolder+1)+ ".jpg")));
	  }
	  catch(Exception e) {
	    println("Image failed to Save, probably just a corrupt one");
	  }
	}
	public void play () {
	  if (glitchLoop == false) {
	    glitchLoop = true;
	    setLock(cp5.getController("saveframes"), false);
	    saveFramesLoop=false;
	  }
	  else {
	    setLock(cp5.getController("saveframes"), true);
	    saveImageLoopToggle.setState(false);
	    glitchLoop=false;
	    saveFramesLoop=false;
	  }
	}
	public void saveframes() {
	  if (saveFramesLoop == false) {
	    saveFramesLoop = true;
	  }
	  else {
	    saveFramesLoop=false;
	  }
	}
	void setLock(Controller<?> theController, boolean theValue) {
	  theController.setLock(theValue);
	  if (theValue) {
	    theController.setColorBackground(color(100, 100));
	  } 
	  else {
	    theController.setColorBackground(color(cp5Color));
	  }
	}
	public void checkForNewImage() {

	  if (!currentImageName.equals((String)cp5.get(Textfield.class, "imageurl").getText())) {
	    b = loadBytes(cp5.get(Textfield.class, "imageurl").getText());
	    bGlitched = loadBytes(cp5.get(Textfield.class, "imageurl").getText());
	    currentImageName = cp5.get(Textfield.class, "imageurl").getText();
	  }
	  else {
	  }
	  
	}
	public void glitch() {
	  checkForNewImage();

	  generateNewGlitch();
	  configureUI();
	  //println(headerOffset);
	}
	public void defaultImage() {
	  imageUrlTextField.setText("2.jpg");
	  urlValue = "2.jpg";
	  //cp5.get(Textfield.class,"textValue")="2.jpg";
	}
	public void tryAGlitch() {
	  try { 
	    glitch();
	    if (saveFramesLoop == true) {
	    saveImage();
	  }
	  }
	  catch(Exception e) {
	    System.err.println("Can't create image from buffer");
	    //e.printStackTrace();
	  }
	}
	public void pasteurl() {
	  imageUrlTextField.setText(GClip.paste());
	  imageUrlTextField.setPosition(img.width+20, 20);
	  urlValue = GClip.paste();
	  tryAGlitch();
	}
	public void keyPressed() {
	  if (key == 'v') {
	    //println("paste");
	    imageUrlTextField.setText(GClip.paste());
	    imageUrlTextField.setPosition(img.width+20, 20);
	    urlValue = GClip.paste();
	  }
	  if (key == 's') {
	    saveImage();
	  }
	}

}
