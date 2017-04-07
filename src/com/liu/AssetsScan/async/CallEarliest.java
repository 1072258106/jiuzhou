package com.liu.AssetsScan.async;

public abstract interface CallEarliest<T>
{
  public abstract void onCallEarliest()
    throws Exception;
}
