package com.example.hzg.videovr;

import android.util.Log;
import android.util.SparseArray;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static android.content.ContentValues.TAG;

/**
 * Created by hzg on 2017/2/27.
 */

public class VideoReaderSet implements VideoReader {
    private LinkedHashSet<Integer> sensorSet;
    private  ArrayList<Integer> sensorList;
    private SparseArray<Mat> data;
    private VideoCapture videoCapture;
    private  String videoMessage;
    VideoReaderSet(String filename)
    {
        try {
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(filename+".vr"));
            sensorSet= (LinkedHashSet<Integer>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        videoCapture=new VideoCapture(filename);
        int count = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
        int width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int heiht = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        videoMessage="视频总帧数:"+count+"  尺寸："+width+"*"+heiht;
        data=new SparseArray<>();
        for (int i:sensorSet)
        {
            Mat mat=new Mat();
            boolean readed=videoCapture.read(mat);
            //颜色转换 将BGR转为RGB
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);
            data.put(i, mat);
            Log.d(TAG, "put Mat isread?" + readed + " x:" + i + " mat:" + mat);
        }
        sensorList=new ArrayList<>(sensorSet);
        System.out.println(sensorList);
        videoCapture.release();
    }
    @Override
    public int getSensor(int position) {
        return sensorList.get(position);
    }

    @Override
    public Mat getMat(int position) {
        return data.get(position);
    }

    @Override
    public int size() {
        return sensorSet.size();
    }

    @Override
    public String getVideoMessage() {
        return videoMessage;
    }
}
