package cn.meshee.freechat.ui.fragment;

public class FragmentFactory {

    static FragmentFactory mInstance;

    private RecentMessageFragment mRecentMessageFragment;

    private ContactsFragment mContactsFragment;

    private DiscoveryFragment mDiscoveryFragment;

    private MeFragment mMeFragment;

    private FragmentFactory() {
    }

    public static FragmentFactory getInstance() {
        if (mInstance == null) {
            synchronized (FragmentFactory.class) {
                if (mInstance == null) {
                    mInstance = new FragmentFactory();
                }
            }
        }
        return mInstance;
    }

    public RecentMessageFragment getRecentMessageFragment() {
        if (mRecentMessageFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mRecentMessageFragment == null) {
                    mRecentMessageFragment = new RecentMessageFragment();
                }
            }
        }
        return mRecentMessageFragment;
    }

    public ContactsFragment getContactsFragment() {
        if (mContactsFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mContactsFragment == null) {
                    mContactsFragment = new ContactsFragment();
                }
            }
        }
        return mContactsFragment;
    }

    public DiscoveryFragment getDiscoveryFragment() {
        if (mDiscoveryFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mDiscoveryFragment == null) {
                    mDiscoveryFragment = new DiscoveryFragment();
                }
            }
        }
        return mDiscoveryFragment;
    }

    public MeFragment getMeFragment() {
        if (mMeFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mMeFragment == null) {
                    mMeFragment = new MeFragment();
                }
            }
        }
        return mMeFragment;
    }
}
