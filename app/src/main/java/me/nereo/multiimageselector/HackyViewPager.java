package me.nereo.multiimageselector;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class HackyViewPager extends ViewPager
{

	public HackyViewPager(Context context)
	{
		super(context);
	}

	public HackyViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		try
		{
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			return false;
		} catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y)
	{
		if (v instanceof ImageViewTouch)
		{
			return ((ImageViewTouch) v).canScroll(dx);
		} else
		{
			if (v instanceof HackyViewPager)
			{
				try
				{
					ViewGroup viewGroup = (ViewGroup) ((HackyViewPager) v).getChildAt(((HackyViewPager) v).getCurrentItem());
					if (viewGroup == null)
					{
						return false;
					} else
					{
						for (int i = 0; i < viewGroup.getChildCount(); i++)
						{
							View imageViewTouch = viewGroup.getChildAt(i);
							if (imageViewTouch instanceof ImageViewTouch
									&& !(((ImageViewTouch) imageViewTouch).getDrawable() instanceof BitmapDrawable))
							{// 当imageViewTouch没有显示图片时默认viewpager时可以滚动的，不然会出现imageViewTouch没有显示图片时导致viewpager无法滚动
								return false;
							}
						}
						return true;
					}

				} catch (Exception e)
				{
					// TODO: handle exception
					return super.canScroll(v, checkV, dx, x, y);
				}

			}
			return super.canScroll(v, checkV, dx, x, y);
		}
	}
}
