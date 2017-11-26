package edu.nju.ws.classify;

import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.standard.NumberUpSupported;

public class emotion {
	static Map<String,Integer> emotionMap1 = new  HashMap<String, Integer>();
	static Map<String,Integer> emotionMap2 = new  HashMap<String, Integer>();
	static Map<String,Integer> emotionMap3 = new  HashMap<String, Integer>();
	static Map<String,Integer> emotionMap4 = new  HashMap<String, Integer>();
	static Map<String,Integer> emotionMap5 = new  HashMap<String, Integer>();
	static Map<Integer,Integer> emotion_count = new  HashMap<Integer, Integer>();
	static int the_sum_word = 0;


	static void loadFile(String trainfilename,String labelfilename) throws NumberFormatException, IOException{
		File trainfile = new File(trainfilename);
		InputStreamReader trainreader = new InputStreamReader(new FileInputStream(trainfile));
		BufferedReader br = new BufferedReader(trainreader);
		
		File labelfile = new File(labelfilename);
		InputStreamReader labelreader = new InputStreamReader(new FileInputStream(labelfile));
		BufferedReader br1 = new BufferedReader(labelreader);
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		String line =null;
		while((line = br1.readLine())!= null){
			int number = line.charAt(0)-'0';
			numbers.add(number);
			if(emotion_count.get(number)!=null)
				emotion_count.put(number, emotion_count.get(number)+1);
			else{
				emotion_count.put(number,1);
			}
		}
		int count = 0;
		//取出对应标签的那一行
		while((line = br.readLine())!= null){	
			line = line.replaceAll(",", "");
			String[] strs = line.split(" ");
			int num = numbers.get(count);
			for(String str:strs){
				the_sum_word++;
				switch (num)
				{
					case 1:
						if(emotionMap1.get(str)!=null)
							emotionMap1.put(str,emotionMap1.get(str)+1);
						else {
							emotionMap1.put(str,1);
						}
					break;
					case 2:
						if(emotionMap2.get(str)!=null)
							emotionMap2.put(str,emotionMap2.get(str)+1);
						else {
							emotionMap2.put(str,1);
						}
					break;
					case 3:
						if(emotionMap3.get(str)!=null)
							emotionMap3.put(str,emotionMap3.get(str)+1);
						else {
							emotionMap3.put(str,1);
						}
					break;
					case 4:
						if(emotionMap4.get(str)!=null)
							emotionMap4.put(str,emotionMap4.get(str)+1);
						else {
							emotionMap4.put(str,1);
						}
					break;
					case 5:
						if(emotionMap5.get(str)!=null)
							emotionMap5.put(str,emotionMap5.get(str)+1);
						else {
							emotionMap5.put(str,1);
						}
					break;
				}
			}
			count++;
		}
		
		return ;
	}

	static double compute_Pw(Map<String,Integer> emotionMap,String[] strs,double p_c){
		double Map_sum_value = 0;
		double P_score = 1;
		double chushu = 0;
		double parameter1=5.5 ;
		double  parameter2=1.02;
		for (Integer value : emotionMap.values()) {  		    
			Map_sum_value = Map_sum_value + value;
		} 
		
		for(String str:strs){
			double num=0;
			if(emotionMap.get(str)!=null){
				num = emotionMap.get(str)+1*parameter1;
				chushu =parameter2*the_sum_word+ Map_sum_value;
			}
			else{
				num =1*parameter1;
				chushu = parameter2*the_sum_word+Map_sum_value;
			}
			P_score *= num/chushu;
		}
		
		return p_c*P_score ;	
	}

	
	
	static int compute_probability(String sentence){
		int score = 0;
		sentence = sentence.replaceAll(",", "");
		String[] strs = sentence.split(" ");
		double [] P_c=new double[6];
		for(int i=1;i<=5;i++){
			double sum = emotion_count.get(1)+emotion_count.get(2)+emotion_count.get(3)+emotion_count.get(4)+emotion_count.get(5);
			int num= emotion_count.get(i);
			P_c[i] = (double)num/sum;
		}
		List<Double> probility = new ArrayList<Double>();
		probility.add(compute_Pw(emotionMap1,strs,P_c[1]));
		probility.add(compute_Pw(emotionMap2,strs,P_c[2]));
		probility.add(compute_Pw(emotionMap3,strs,P_c[3]));
		probility.add(compute_Pw(emotionMap4,strs,P_c[4]));
		probility.add(compute_Pw(emotionMap5,strs,P_c[5]));
		Double temp =0.0;
		for(int i=0;i<probility.size();i++){
			if(probility.get(i)>temp){
				temp = probility.get(i);
				score = i;
			}
		}

		return score+1;
		
	}
	
	
	static double evaluate(String trainfilename,String labelfilename) throws IOException{
		File trainfile = new File(trainfilename);
		InputStreamReader trainreader = new InputStreamReader(new FileInputStream(trainfile));
		BufferedReader br = new BufferedReader(trainreader);
		
		File labelfile = new File(labelfilename);
		InputStreamReader labelreader = new InputStreamReader(new FileInputStream(labelfile));
		BufferedReader br1 = new BufferedReader(labelreader);
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		String line =null;
		while((line = br1.readLine())!= null){
			int number = line.charAt(0)-'0';
			numbers.add(number);
		}
		int i=0;
		int count=0;
		while((line = br.readLine())!= null){	
			line = line.replaceAll(",", "");
			if(compute_probability(line)==numbers.get(i)){
				count++;
			}
			i++;
		}
		double acc = (double)count/numbers.size();
		return acc;
	}
	
	
	static void test(String trainfilename,String labelfilename) throws IOException{
		File trainfile = new File(trainfilename);
		InputStreamReader trainreader = new InputStreamReader(new FileInputStream(trainfile));
		BufferedReader br = new BufferedReader(trainreader);
		
		File labelfile = new File(labelfilename);
		PrintStream ps = new PrintStream(new FileOutputStream(labelfile));
		String line =null;
		while((line = br.readLine())!= null){	
			line = line.replaceAll(",", "");
	        ps.println(compute_probability(line));
		}
		return ;
	}
	
	
	public static void main(String[] args) throws NumberFormatException, IOException{
		loadFile("data/train_x.txt","data/train_y.txt");
		//loadFile("data/tx.txt","data/ty.txt");
		System.out.println(evaluate("data/dev_x.txt","data/dev_y.txt"));
		test("data/test_x.txt","MF1733086.txt");
	}
	
	
}
