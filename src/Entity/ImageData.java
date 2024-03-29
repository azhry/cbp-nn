/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import Control.MathFx;
import org.opencv.core.Core;

/**
 *
 * @author Azhary Arliansyah
 */
public class ImageData {
    
    private final BufferedImage rawImg;
    private final Mat img;
    
    private Mat grayImg;
    private Mat filteredImg;
    private Mat rawImgData;
    private double[] filteredData;
    private double[] cbpData;
    private double[] data;
    private String label;
    private Mat cbpMat;
    private Mat sobelMat;
    private Mat segmentationMat;
    
    public ImageData(String path, Mat kernel, String label) {
        this.label = label;
        this.img = Imgcodecs.imread(path);
        this.rawImg = new BufferedImage(this.img.width(), this.img.height(), 
                BufferedImage.TYPE_INT_ARGB);
        this.grayImg = new Mat(this.img.rows(), this.img.cols(), 
                CvType.CV_8UC1);
        Imgproc.cvtColor(this.img, this.grayImg, Imgproc.COLOR_RGB2GRAY);
        
        this.filteredImg = new Mat(this.grayImg.rows(), this.grayImg.cols(), 
                this.img.type());
        Imgproc.filter2D(this.grayImg, this.filteredImg, this.grayImg.type(), 
                kernel);
        
        this.filteredData = new double[this.filteredImg.rows() * 
                this.filteredImg.cols()];
        int i = 0;
        for (int j = 0; j < this.filteredImg.rows(); j++) {
            for (int k = 0; k < this.filteredImg.cols(); k++) {
                for (int l = 0; l < this.filteredImg.channels(); l++) {
                    this.filteredData[i] = this.filteredImg.get(j, k)[l];
                    i++;
                } 
            }
        }
    }
    
    public ImageData(String path, String label) {
        this.label = label;
        this.img = Imgcodecs.imread(path);
        this.rawImg = new BufferedImage(this.img.width(), this.img.height(), 
                BufferedImage.TYPE_INT_ARGB);
        
        this.grayImg = new Mat(this.img.rows(), this.img.cols(), 
                CvType.CV_8UC1);
        Imgproc.cvtColor(this.img, this.grayImg, Imgproc.COLOR_RGB2GRAY);
        
        this.rawImgData = new Mat(this.grayImg.rows(), this.grayImg.cols(), 
                this.img.type());
        
        this.data = new double[this.rawImgData.rows() * 
                this.rawImgData.cols() * this.rawImgData.channels()];
        int i = 0;
        for (int j = 0; j < this.rawImgData.rows(); j++) {
            for (int k = 0; k < this.rawImgData.cols(); k++) {
                for (int l = 0; l < this.rawImgData.channels(); l++) {
                    this.data[i] = this.rawImgData.get(j, k)[l];
                    i++;
                } 
            }
        }
        
        this.sobel();
        this.adaptiveThresholding();
        this.cbp(8, 1, 8);
        
        this.cbpData = new double[this.cbpMat.rows() * 
                this.cbpMat.cols() * this.cbpMat.channels()];
        i = 0;
        for (int j = 0; j < this.cbpMat.rows(); j++) {
            for (int k = 0; k < this.cbpMat.cols(); k++) {
                for (int l = 0; l < this.cbpMat.channels(); l++) {
                    this.cbpData[i] = this.cbpMat.get(j, k)[l];
                    i++;
                } 
            }
        }
    }
    
    public void filterImg(Mat kernel) {
        this.grayImg = new Mat(this.img.rows(), this.img.cols(), 
                CvType.CV_8UC1);
        Imgproc.cvtColor(this.img, this.grayImg, Imgproc.COLOR_RGB2GRAY);
        
        this.filteredImg = new Mat(this.grayImg.rows(), this.grayImg.cols(), 
                this.img.type());
        Imgproc.filter2D(this.grayImg, this.filteredImg, this.grayImg.type(), 
                kernel);
        
        this.filteredData = new double[this.filteredImg.rows() * 
                this.filteredImg.cols()];
        int i = 0;
        for (int j = 0; j < this.filteredImg.rows(); j++) {
            for (int k = 0; k < this.filteredImg.cols(); k++) {
                for (int l = 0; l < this.filteredImg.channels(); l++) {
                    this.filteredData[i] = this.filteredImg.get(j, k)[l];
                    i++;
                } 
            }
        }
    }
    
    private List<List<Integer[]>> getSurroundingPairSet(int x, int y, int r) {
        
        List<List<Integer[]>> pairSet = new ArrayList<>();
        List<Integer[]> lower = new ArrayList<>();
        List<Integer[]> upper = new ArrayList<>();
        
        int currentX = -r;
        int currentY = -r;
        
        for (int i = 0; currentY <= r; i++) {
            upper.add(new Integer[] {x + currentX, y + currentY + i});
            lower.add(new Integer[] {x - currentX, y - currentY - i});
            if (currentY + i >= r) {
                currentY += i;
                break;
            }
        }
        
        for (int i = 1; currentX <= r - 1; i++) {
            upper.add(new Integer[] {x + currentX + i, y + currentY});
            lower.add(new Integer[] {x - currentX - i, y - currentY});
            if (currentX + i >= r - 1) {
                break;
            }
        }
        
        pairSet.add(lower);
        pairSet.add(upper);
        return pairSet;
    }
    
    private void adaptiveThresholding() {
        this.segmentationMat = new Mat();

        Imgproc.adaptiveThreshold(this.sobelMat, this.segmentationMat, 125, 
               Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 11, 12);
    }
    
    private void sobel() {
        Mat grayMat = new Mat();
        this.sobelMat = new Mat(); //Mat to store the final result

        //Matrices to store gradient and absolute gradient respectively
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();

        Mat grad_y = new Mat();
        Mat abs_grad_y = new Mat();

        //Calculating gradient in horizontal direction
        Imgproc.Sobel(this.grayImg, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);

        //Calculating gradient in vertical direction
        Imgproc.Sobel(this.grayImg, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);

        //Calculating absolute value of gradients in both the direction
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        //Calculating the resultant gradient
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, this.sobelMat);
    }
    
    public void cbp(int m, int r, int t) {
        
        int PAIRS = 4;
        Mat cbpMat = 
                new Mat(this.segmentationMat.rows(), 
                        this.segmentationMat.cols(), 
                        this.segmentationMat.type());
                
        for (int i = 0; i < this.segmentationMat.rows(); i++) {
            for (int j = 0; j < this.segmentationMat.cols(); j++) {
                List<List<Integer[]>> pairSet = this.getSurroundingPairSet(i, j, r);
                List<Integer[]> lower = pairSet.get(0);
                List<Integer[]> upper = pairSet.get(1);
                double[] absSurrounds = new double[lower.size()];
                for (int k = 0; k < lower.size(); k++) {
                    Integer[] l = lower.get(k);
                    Integer[] u = upper.get(k);
                    double lowerValue = 0;
                    double upperValue = 0;
                    if (l[0] >= 0 && l[1] >= 0 && 
                            l[0] < this.segmentationMat.rows() && 
                            l[1] < this.segmentationMat.cols()) {
                        lowerValue = this.segmentationMat.get(l[0], l[1])[0];
                    }
                    if (u[0] >= 0 && u[1] >= 0 && 
                            u[0] < this.segmentationMat.rows() && 
                            u[1] < this.segmentationMat.cols()) {
                        upperValue = this.segmentationMat.get(u[0], u[1])[0];
                    }
                    absSurrounds[k] = Math.abs(lowerValue - upperValue);
                    
                }
                
                double cbpVal = this.segmentationMat.get(i, j)[0] - 
                        ((this.segmentationMat.get(i, j)[0] + 
                        MathFx.sum(absSurrounds)) / ((PAIRS * r) + 1));
                int[] absSurroundsInt = new int[absSurrounds.length + 1];
                for (int k = 0; k < absSurroundsInt.length - 1; k++) {
                    if (absSurrounds[k] > t) {
                        absSurroundsInt[k] = 1;
                    }
                    else {
                        absSurroundsInt[k] = 0;
                    }
                }
                
                if (cbpVal > t) {
                    absSurroundsInt[absSurrounds.length] = 1;
                }
                else {
                    absSurroundsInt[absSurrounds.length] = 0;
                }
                
                double[] absSurroundsPow = new double[absSurroundsInt.length];
                for (int k = 0; k < absSurroundsPow.length; k++) {
                    absSurroundsPow[k] = absSurroundsInt[k] * Math.pow(2, k);
                }
                
                cbpMat.put(i, j, new double[] {MathFx.sum(absSurroundsPow), 
                    MathFx.sum(absSurroundsPow), MathFx.sum(absSurroundsPow)});
            }
        }
        
        this.cbpMat = cbpMat;
    }
    
    public Mat getImg() {
        return this.img;
    }
    
    public BufferedImage getRawImg() {
        return this.rawImg;
    }
    
    public Mat getGrayImg() {
        return this.grayImg;
    }
    
    public Image getBufferCbpImg() {
        byte[] data = new byte[this.cbpMat.width() * 
                this.cbpMat.height() * (int)this.cbpMat.elemSize()];
        this.cbpMat.get(0, 0, data);
        int type;
        if (this.cbpMat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }
        else {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        
        BufferedImage im = new BufferedImage(this.cbpMat.width(), 
                this.cbpMat.height(), type);
        im.getRaster().setDataElements(0, 0, this.cbpMat.width(), 
                this.cbpMat.height(), data);
        return im;
    }
    
    public Image getBufferSobelFilteredImg() {
        byte[] data = new byte[this.sobelMat.width() * 
                this.sobelMat.height() * (int)this.sobelMat.elemSize()];
        this.sobelMat.get(0, 0, data);
        int type;
        if (this.sobelMat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }
        else {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        
        BufferedImage im = new BufferedImage(this.sobelMat.width(), 
                this.sobelMat.height(), type);
        im.getRaster().setDataElements(0, 0, this.sobelMat.width(), 
                this.sobelMat.height(), data);
        return im;
    }
    
    public Image getBufferSegmentedImg() {
        byte[] data = new byte[this.segmentationMat.width() * 
                this.segmentationMat.height() * (int)this.segmentationMat.elemSize()];
        this.segmentationMat.get(0, 0, data);
        int type;
        if (this.segmentationMat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }
        else {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        
        BufferedImage im = new BufferedImage(this.segmentationMat.width(), 
                this.segmentationMat.height(), type);
        im.getRaster().setDataElements(0, 0, this.segmentationMat.width(), 
                this.segmentationMat.height(), data);
        return im;
    }
    
    
    public Image getBufferFilteredImg() {
        byte[] data = new byte[this.filteredImg.width() * 
                this.filteredImg.height() * (int)this.filteredImg.elemSize()];
        this.filteredImg.get(0, 0, data);
        int type;
        if (this.filteredImg.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        }
        else {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        
        BufferedImage im = new BufferedImage(this.filteredImg.width(), 
                this.filteredImg.height(), type);
        im.getRaster().setDataElements(0, 0, this.filteredImg.width(), 
                this.filteredImg.height(), data);
        return im;
    }
    
    public Mat getFilteredImg() {
        return this.filteredImg;
    }
    
    public double[] getFilteredData() {
        return this.filteredData;
    }
    
    public double[] getCbpData() {
        return this.cbpData;
    }
    
    public double[] getData() {
        return this.data;
    }
    
    public String getLabel() {
        return this.label;
    }
}
