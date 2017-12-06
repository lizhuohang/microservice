package com.lzh.micro.framework.utils;

import java.util.Random;


public class RandomUtil {
	
	/**
	 * @return
	 * @author lizhuohang
	 * @function 概率3/7的概率时prob传入3，max传入7
	 */
	public static boolean getProbRandom(int prob,int max) {
		Random random = new Random();
		return random.nextInt(max) < prob;
	}
	
	/**
	 * @param hight
	 * @param low
	 * @return
	 * @author lizhuohang
	 * @function 返回一个随机数，该数最小值等于low，最大值等于hight
	 */
	public static int getRandomNum(int hight,int low) {
		Random random = new Random();
		return random.nextInt(hight - low+1) + low;
	}
}
