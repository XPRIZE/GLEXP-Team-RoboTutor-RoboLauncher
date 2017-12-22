//*********************************************************************************
//
//    Copyright(c) 2016 Carnegie Mellon University. All Rights Reserved.
//    Copyright(c) Kevin Willows All Rights Reserved
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//*********************************************************************************

package cmu.xprize.rthomescreen.startup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ViewAnimator;

import java.util.ArrayList;

import cmu.xprize.rthomescreen.R;
import cmu.xprize.util.ILogManager;

public class CMasterContainer extends RelativeLayout {

    protected Context        mContext;
    protected int            insertNdx;

    final private String       TAG       = "TMasterAnimatorLayout";


    public CMasterContainer(Context context) {
        super(context);
        init(context, null);
    }

    public CMasterContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        mContext    = context;
    }


    @Override
    public void addView(View newView) {

        insertNdx = indexOfChild((View)newView);

        if(insertNdx == -1) {
            insertNdx = super.getChildCount();
            super.addView((View) newView, insertNdx);
        }

        Log.d(TAG, "ADD > Child Count: " + getChildCount() );
    }


    public void addAndShow(View newView) {

        addView(newView);
    }


    @Override
    public void removeView(View delView) {

        super.removeView((View) delView);

        Log.d(TAG, "REMOVE > Child Count: " + getChildCount() );
    }


}
