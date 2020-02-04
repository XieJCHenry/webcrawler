package org.jccode.webcrawler.spiders;

/**
 * SpiderStopStrategy
 * <p>
 * 三种模式：
 * 1、只执行添加的任务
 * 2、执行成功的任务数到达给定值后停止
 * 3、执行一定时间（暂不实现）
 * 4、不限时
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/4 17:01
 * @Version 1.0
 **/
public enum SpiderStopStrategy {


    ONLY_INIT_TASK, EXCEPTED_COUNT, UNLIMITED;

}
