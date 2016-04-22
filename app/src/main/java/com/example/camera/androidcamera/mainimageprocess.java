package com.example.camera.androidcamera;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by fufamilyDeskWin7 on 2016/3/16.
 */
public class mainimageprocess {

    private static final String TAG = "OCVSample::Activity";
    //取我們想要的區域
    public static Mat get_halfimg(Mat orgimg, int vertical) {
        //vertical=1 horizontal=0
//        Mat halforg = new Mat(orgimg.size(), CvType.CV_8UC4);
        if (vertical == 1) {
//            halforg = orgimg.submat(0, orgimg.height(), 0, orgimg.width() / 3);
            return orgimg.submat(0, orgimg.height(), 0, orgimg.width() / 3);

        } else {
//            halforg = orgimg.submat(0, orgimg.height() / 3, 0, orgimg.width());
            return orgimg.submat(0, orgimg.height() / 3, 0, orgimg.width());
//            return halforg;
        }
//        return halforg;
    }

    //RGB 轉換成 HSV
    public static Mat RGB2HSV(Mat Rgb) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(Rgb, hsv, Imgproc.COLOR_RGB2HSV);
        return hsv;
    }

    //從HSV提出HSV_S
    public static Mat get_HSV_s(Mat HSV) {
        Mat hsv_s = new Mat();
        Core.extractChannel(HSV, hsv_s, 1);
        return hsv_s;
    }
    //從HSV提出HSV_S
    public static Mat get_HSV_h(Mat HSV) {
        Mat img = new Mat();
        Core.extractChannel(HSV, img, 0);
        return img;
    }
    //從HSV提出HSV_S
    public static Mat get_HSV_v(Mat HSV) {
        Mat img = new Mat();
        Core.extractChannel(HSV, img, 2);
        return img;
    }
    //HSV_S points value
    public static int get_HSVs_points_value(Mat HSV_s) {
        Mat mask_s = new Mat();
        Core.inRange(HSV_s, new Scalar(76), new Scalar(255), mask_s);
        mask_s = hsv_s_erode_dilate(mask_s);
//        get_HSV_s.copyTo(get_HSV_s, mask_s); //將原圖片經由遮罩過濾後，得到結果dst
        return Core.countNonZero(mask_s);
    }

    //Gauss7 Canny(80,100)
    public static Mat G7_C80100_img(Mat halfimg) {
        Mat G7_C80100 = new Mat();
        Imgproc.GaussianBlur(halfimg, G7_C80100, new Size(5, 5), 3, 3);
        Imgproc.Canny(G7_C80100, G7_C80100, 80, 100);
        return G7_C80100;
    }

    //Gauss11 Canny(80,100)
    public static Mat G11_C80100_img(Mat halfimg) {
        Mat G11_C80100 = new Mat();
        Imgproc.GaussianBlur(halfimg, G11_C80100, new Size(11, 11), 3, 3);
        Imgproc.Canny(G11_C80100, G11_C80100, 80, 100);
        return G11_C80100;
    }

    public static int get_img_onelayer_value(Mat onelayer_img) {
        return Core.countNonZero(onelayer_img);
    }

    public static Mat RGB_cut_HSVs_return_rgb(Mat one_channel_img) {
        Mat mask_s = new Mat(one_channel_img.size(), CvType.CV_8UC1);
//        Core.extractChannel(hsv, hsv_s, 1);
//        Log.i(TAG, "HSV: do mask");
        Core.inRange(one_channel_img, new Scalar(76), new Scalar(255), mask_s);
//        Log.i(TAG, "HSV: copy to");

        one_channel_img.copyTo(one_channel_img, mask_s); //將原圖片經由遮罩過濾後，得到結果dst
        Imgproc.cvtColor(one_channel_img, one_channel_img, Imgproc.COLOR_GRAY2RGBA);
//        Log.i(TAG, "HSV: finish!");
        //output Mat is hsv_s
        return one_channel_img;
    }
    public static Mat hsv_s_erode_dilate(Mat img) {
        Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4)), new Point(-1, -1), 1);
        Imgproc.dilate(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4)), new Point(-1, -1), 1);
        return img;
    }
    //----------------------------------------------------------------------------------------------
    //取膚色區域↓↓↓ 用hsv
    public static Mat body_hsv(Mat img) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2HSV);
        Mat hsv_h = new Mat();
        Mat hsv_s = new Mat();
        Core.extractChannel(hsv, hsv_h, 0);
        Core.extractChannel(hsv, hsv_s, 1);

        Mat hsv_h_mask = new Mat();
        Mat hsv_s_mask = new Mat();
        Core.inRange(hsv_h, new Scalar(4), new Scalar(70), hsv_h_mask);
        Core.inRange(hsv_s, new Scalar(20), new Scalar(128), hsv_s_mask);
        Imgproc.erode(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.erode(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);

        Imgproc.erode(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.erode(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);

        Mat bodyrgb = new Mat();
        img.copyTo(bodyrgb, hsv_h_mask);
//        bodyrgb.copyTo(bodyrgb, hsv_s_mask);
//        img.copyTo(bodyrgb, hsv_s_mask);
        return bodyrgb;
    }
    //取膚色區域↓↓↓ 用YCbCr
    public static Mat body_YCbCr(Mat img) {
        int avg_cb = 120;//YCbCr顏色空間膚色cb的平均值
        int avg_cr = 155;//YCbCr顏色空間膚色cr的平均值
        int skinRange = 22;//YCbCr顏色空間膚色的範圍


        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2YCrCb);
        Mat cr = new Mat();
        Mat cb = new Mat();
        Core.extractChannel(hsv, cr, 1);
        Core.extractChannel(hsv, cb, 2);

        Mat cr_mask = new Mat();
        Mat cb_mask = new Mat();
        Core.inRange(cr, new Scalar(avg_cr - skinRange), new Scalar(avg_cr + skinRange), cr_mask);
        Core.inRange(cb, new Scalar(avg_cb - skinRange), new Scalar(avg_cb + skinRange), cb_mask);

        Imgproc.erode(cr_mask, cr_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);
        Imgproc.dilate(cr_mask, cr_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);
        Imgproc.erode(cb_mask, cb_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);
        Imgproc.dilate(cb_mask, cb_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);

        Mat bodyrgb = new Mat();
        img.copyTo(bodyrgb, cr_mask);
        bodyrgb.copyTo(bodyrgb, cb_mask);
        return bodyrgb;
    }
    //取膚色區域↓↓↓ 用YCbCr
    public static int get_body_YCbCr(Mat img) {
        int avg_cb = 120;//YCbCr顏色空間膚色cb的平均值
        int avg_cr = 155;//YCbCr顏色空間膚色cr的平均值
        int skinRange = 22;//YCbCr顏色空間膚色的範圍


        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2YCrCb);
        Mat cr = new Mat();
        Mat cb = new Mat();
        Core.extractChannel(hsv, cr, 1);
        Core.extractChannel(hsv, cb, 2);

        Mat cr_mask = new Mat();
        Mat cb_mask = new Mat();
        Core.inRange(cr, new Scalar(avg_cr - skinRange), new Scalar(avg_cr + skinRange), cr_mask);
        Core.inRange(cb, new Scalar(avg_cb - skinRange), new Scalar(avg_cb + skinRange), cb_mask);

        Imgproc.erode(cr_mask, cr_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);
        Imgproc.dilate(cr_mask, cr_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);
        Imgproc.erode(cb_mask, cb_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);
        Imgproc.dilate(cb_mask, cb_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 20)), new Point(-1, -1), 1);

        Mat bodygray = new Mat();
        cr_mask.copyTo(bodygray,cb_mask);
        return Core.countNonZero(bodygray);
    }
//----------------------------------------------------------------------------------------------------------

    public static Mat sobel_outputgray_Y(Mat img) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(img, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp, new Size(5, 5), 3, 3);
        Imgproc.Sobel(tmp, tmp, CvType.CV_8U, 0, 1);
        Core.convertScaleAbs(tmp, tmp, 10, 0);
        Mat onelayer = new Mat();
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.erode(tmp, tmp, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(4, 2)), new Point(-1, -1), 3);
        Imgproc.erode(tmp, tmp, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 1)), new Point(-1, -1), 1);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.dilate(tmp, tmp, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(5, 2)), new Point(-1, -1), 2);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        return onelayer;
    }

    public static Mat sobel_outputgray_X(Mat img) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(img, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp, new Size(5, 5), 3, 3);
        Imgproc.Sobel(tmp, tmp, CvType.CV_8U, 1, 0);
        Core.convertScaleAbs(tmp, tmp, 10, 0);
        Mat onelayer = new Mat();
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2, 4)), new Point(-1, -1), 3);
        Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 2)), new Point(-1, -1), 1);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.dilate(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2, 5)), new Point(-1, -1), 2);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        return onelayer;
    }
    //有無地面與牆壁接縫↓↓↓
    public static Boolean getcol(Mat img) {//垂直
        int widthvalue = img.width()/10;
        int[] num = new int[9];
        num[0] = Core.countNonZero(img.col(widthvalue));
        num[1] = Core.countNonZero(img.col(widthvalue * 2));
        num[2] = Core.countNonZero(img.col(widthvalue * 3));
        num[3] = Core.countNonZero(img.col(widthvalue * 4));
        num[4] = Core.countNonZero(img.col(widthvalue * 5));
        num[5] = Core.countNonZero(img.col(widthvalue * 6));
        num[6] = Core.countNonZero(img.col(widthvalue * 7));
        num[7] = Core.countNonZero(img.col(widthvalue * 8));
        num[8] = Core.countNonZero(img.col(widthvalue * 9));
        int min, max, value;
        min=max=num[0];
        for(char i=0;i<num.length;i++) {
            if(num[i]>max)
                max=num[i];
            if(num[i]<min)
                min=num[i];
        }
        value = max - min;
        if (value < 7 && (max + min) /2 < 5 && (max + min) /2 > 1) {
            //有牆壁跟地面的線
            return true;
        }
        else {
            return false;
        }
    }
    //有無柱子↓↓↓
    public static Boolean getrow(Mat img) {//水平

        int widthvalue = img.width()/6;
        int[] num = new int[6];
        num[0] = Core.countNonZero(img.row(widthvalue));
        num[1] = Core.countNonZero(img.row(widthvalue * 2));
        num[2] = Core.countNonZero(img.row(widthvalue * 3));
        num[3] = Core.countNonZero(img.row(widthvalue * 4));
        num[4] = Core.countNonZero(img.row(widthvalue * 5));

        int min, max, value;
        min=max=num[0];
        for(char i=0;i<num.length;i++) {
            if(num[i]>max)
                max=num[i];
            if(num[i]<min)
                min=num[i];
        }
        value = max - min;
        if (value < 7 && (max + min) /2 < 7 && (max + min) /2 > 1) {
            //有柱子的線
            return true;
        }
        else {
            return false;
        }
    }

//去除小石頭↓↓↓

    public static Mat clear_tile(Mat img) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(img, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Sobel(tmp, tmp, CvType.CV_8U, 1, 1);
        Core.convertScaleAbs(tmp, tmp, 10, 0);
        Mat onelayer = new Mat();
        Core.inRange(tmp, new Scalar(240), new Scalar(255), onelayer);

        Imgproc.dilate(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3)), new Point(-1, -1), 3);
        Core.inRange(onelayer, new Scalar(250), new Scalar(255), onelayer);
        Imgproc.erode(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10, 10)), new Point(-1, -1), 1);
        Core.inRange(onelayer, new Scalar(253), new Scalar(255), onelayer);
//        Imgproc.dilate(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7)), new Point(-1, -1), 3);
//        Core.inRange(onelayer, new Scalar(250), new Scalar(255), onelayer);
//        Imgproc.erode(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(20, 20)), new Point(-1, -1), 1);
//        Core.inRange(onelayer, new Scalar(253), new Scalar(255), onelayer);
        Core.bitwise_not(onelayer, onelayer);
//        Mat output = new Mat();
//        img.copyTo(output, onelayer);
//        Imgproc.cvtColor(onelayer, output, Imgproc.COLOR_GRAY2BGRA);
        return onelayer;

    }

//去除小石頭↑↑↑


    //取線
    public static double[][] HoughLines_have_mask(Mat img, int angle_range, Mat G7, Mat G11) {
        //angle_range = 10;

        Mat doimg = new Mat();
        Mat G7_C80100 = new Mat();
//        Mat mask = new Mat();
//        Core.bitwise_xor(G11, G7, mask);
//        Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3)), new Point(-1, -1), 1);
//
//        Core.bitwise_not(mask, mask);
//        Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3)), new Point(-1, -1), 1);

        Imgproc.GaussianBlur(img, G7_C80100, new Size(9, 9), 3, 3);
        Imgproc.Canny(G7_C80100, G7_C80100, 80, 100);
        Imgproc.dilate(G7_C80100, G7_C80100, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(1, 1)), new Point(-1, -1), 1);
//        G7_C80100.copyTo(doimg, mask);
        G7_C80100.copyTo(doimg);

        Mat lines = new Mat();
        int threshold = 60;//40
        int minLineSize = 60;
        int lineGap = 3;//5

        Imgproc.HoughLinesP(doimg, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);
        Imgproc.cvtColor(doimg, doimg, Imgproc.COLOR_GRAY2BGRA);
        Log.i("m", "new line change and get 斜率=====================================");
        double[][] all = new double[6][];
        all[0] = new double[lines.cols()];//x1
        all[1] = new double[lines.cols()];//y1
        all[2] = new double[lines.cols()];//x2
        all[3] = new double[lines.cols()];//y2
        all[4] = new double[lines.cols()];//斜率
        all[5] = new double[180 / angle_range % 1000 + 1];//統計

        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            all[0][x] = vec[0];//x1
            all[1][x] = vec[1];//y1
            all[2][x] = vec[2];//x2
            all[3][x] = vec[3];//y2
            //a=每條線的斜率
            all[4][x] = (Math.atan2((all[1][x] - all[3][x]), (all[2][x] - all[0][x])) * (180 / Math.PI) + 90.0);
            Log.i("mline a", String.valueOf(all[4][x]));
            Log.i("mline hist a", String.valueOf(all[5][(int) all[4][x] / angle_range]));
            //作統計分類 依各種範圍斜率數座做統計
            all[5][(int) all[4][x] / angle_range] += 1;
        }
        return all;
    }

    public static Mat line(Mat img, double[][] all, int angle_range) {

        int line_count = 2;
        int[] find_which_line = new int[angle_range];
        for (int i = 0; i < all[5].length; i++) {
            //hist中的數值在我們想要探討的範圍
            if (all[5][i] <= line_count && all[5][i] > 0) {
                for (int x = 0; x < angle_range; x++) {
                    find_which_line[x] = all[4].length + 1;//初始化 fine which line
                }
                //因hist中以區段做分割 所以要把區段中每個角度都做搜尋
                for (int x = 0; x < angle_range; x++) {
//                    find_which_line[x] = Math.abs(Arrays.binarySearch(a, i * angle_range + x));
//                    Log.i("mline find which line", String.valueOf(i * angle_range + x) + ":  " + x + " " + String.valueOf(Arrays.binarySearch(a, i * angle_range + x)));
                    for (int j = 0; j < all[4].length; j++) {
                        //搜尋a中對應的值 是在第幾個位置
                        if (all[4][j] == i * angle_range + x) {
                            Log.i("mline find which line", String.valueOf(i * angle_range + x) + ":  " + x + " " + String.valueOf(j));
                            find_which_line[x] = j;
                            //畫line
                            Point start = new Point(all[0][j], all[1][j]);
                            Point end = new Point(all[2][j], all[3][j]);
                            Core.line(img, start, end, new Scalar(0, 255, 0), 2);
//                            Log.i("mline find which line", String.valueOf(find_which_line[x]));
                            Log.i("mline draw line", String.valueOf(all[4][j]));
//                            Log.i("mline draw line", String.valueOf(all[4][j]));
                        } else {
                            find_which_line[x] = all[4].length + 1;
                        }
                    }
                }
            }
        }
        return img;
    }


    public static Boolean Have_line( double[][] all) {

        int line_count = 2;
        Boolean output = false;
        for (int i = 0; i < all[5].length; i++) {
            Log.i("mline have line? hist ", String.valueOf(all[5][i]));
            //hist中的數值在我們想要探討的範圍
            if (all[5][i] <= line_count && all[5][i] > 0) {
                output = true;
            }
        }
        Log.i("mline draw have line? ", String.valueOf(output));
        return output;
    }







    public static Mat showmodel_HSV_s(Mat img) {
        Mat hsv = new Mat();
        Mat rbgcut = new Mat();
        Mat hsv_s = new Mat();

        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2HSV);
        Core.extractChannel(hsv, hsv_s, 1);
        hsv.release();
        Mat mask_s = new Mat(hsv_s.size(), CvType.CV_8UC1);
        Core.inRange(hsv_s, new Scalar(76), new Scalar(255), mask_s);
        mask_s = hsv_s_erode_dilate(mask_s);
        img.copyTo(rbgcut, mask_s);
//        Imgproc.cvtColor(mask_s, rbgcut, Imgproc.COLOR_GRAY2BGRA);
        mask_s.release();
        hsv_s.release();
        return rbgcut;
    }

    public static Mat showmodel_G7_C(Mat img) {
        Imgproc.GaussianBlur(img, img, new Size(5, 5), 3, 3);
        Imgproc.Canny(img, img, 80, 100);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGRA);
        return img;
    }
    public static Mat showmodel_G11_C(Mat img) {
        Imgproc.GaussianBlur(img, img, new Size(11, 11), 3, 3);
        Imgproc.Canny(img, img, 80, 100);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGRA);
        return img;
    }
}
