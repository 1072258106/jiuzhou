package com.liu.AssetsScan.async;
public abstract interface Callable<T>
{
  public abstract T call()
    throws Exception;
}
