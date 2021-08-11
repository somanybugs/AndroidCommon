package lhg.common.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import lhg.common.OnBackPressedCallback;

import java.util.List;

public class FragmentHelper {

    public static boolean onBackPressed(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f.isResumed() && f.isAdded() && f.getUserVisibleHint() && !f.isHidden() && !f.isDetached()) {
                    if (f instanceof OnBackPressedCallback) {
                        if (((OnBackPressedCallback) f).onBackPressed()) {
                            return true;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            return true;
        } else {
            return false;
        }
    }

}
