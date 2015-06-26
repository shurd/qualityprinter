import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

public class SVGtoJPG {

    public static void main(String[] args) throws Exception {
    	String s="";
    	Scanner in = new Scanner(System.in);
    	 
        System.out.println("Enter the filename");
        s = in.nextLine();
        
    	BufferedImage image, newimage;
    	BufferedReader reader = new BufferedReader(new FileReader(s+".svg"));
        String line;
        PrintWriter writer = new PrintWriter("newfile.svg", "UTF-8");
        while ((line = reader.readLine()) != null)
        {
        	line = line.replace("white", "black");
        	writer.println(line);
        }
        reader.close();
        writer.close();
    	
    	// Create a JPEG transcoder
        JPEGTranscoder t = new JPEGTranscoder();

        // Set the transcoding hints.
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
                   new Float(.8));
        t.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(1000));
        //changing height, for better resolution do this

        // Create the transcoder input.
        String svgURI = new File("newfile.svg").toURL().toString();
        System.out.println(svgURI);
        TranscoderInput input = new TranscoderInput(svgURI);

        // Create the transcoder output.
        OutputStream ostream = new FileOutputStream(s+".jpg");
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Save the image.
        t.transcode(input, output);

        // Flush and close the stream.
        ostream.flush();
        ostream.close();
       
        
        image = ImageIO.read(new File(s+".jpg"));
    	int width = image.getWidth();
		int height = image.getHeight();
		newimage = new BufferedImage( width, height, image.getType()  );
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				newimage.setRGB( x, height-y-1, image.getRGB( x, y  )  );
			}
		}
		ImageIO.write(newimage, "jpg", new File(s+".jpg"));
		
        System.exit(0);
    }
}