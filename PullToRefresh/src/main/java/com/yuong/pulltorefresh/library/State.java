package com.yuong.pulltorefresh.library;

/**
 * Created by yuandong on 2018/4/22.
 */

public enum State {
    STATUS_PULL_TO_REFRESH,//下拉状态
    STATUS_RELEASE_TO_REFRESH,//释放立即刷新状态
    STATUS_REFRESHING,//正在刷新状态
    STATUS_REFRESH_FINISHED//刷新完成或未刷新状态
}
