package com.vanzstuff.readdit.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.readdit.data.ReadditContract;
import com.vanzstuff.readdit.redditapi.AboutRequest;
import com.vanzstuff.readdit.redditapi.MySubredditRequest;
import com.vanzstuff.redditapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private RequestQueue mVolleyQueue;

    /*Synchronization status for control by syncadapters*/
    public static final int SYNC_STATUS_NONE = 0x0;
    public static final int SYNC_STATUS_RUNNING = 0x1;
    public static final int SYNC_STATUS_UPDATE = 0x2;
    public static final int SYNC_STATUS_DELETE = 0x4;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init();
    }

    private void init() {
        mVolleyQueue = Volley.newRequestQueue(getContext());
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Logger.w("### SYNC ADAPTER ###");
        syncUser(provider);
        syncSubreddit(provider);
    }

    private void syncSubreddit(ContentProviderClient provider) {
        Logger.w("### syncSubreddit ###");
        Cursor cursor = null;
        try {
            cursor = provider.query(ReadditContract.User.CONTENT_URI, new String[]{ ReadditContract.User.COLUMN_ACCESSTOKEN}, null, null, null);
            while (cursor.move(1)) {
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                MySubredditRequest request = MySubredditRequest.newInstance(MySubredditRequest.PATH_SUBSCRIBER, null, null, -1, 50, future, future, cursor.getString(0));
                mVolleyQueue.add(request);
                JSONObject result = future.get();
                ContentValues[] subredditsValues = loadSubreddits(result);
                int rowsCount = provider.bulkInsert(ReadditContract.Subreddit.CONTENT_URI, subredditsValues);
                if ( rowsCount != subredditsValues.length )
                    Logger.e(String.format("subreddits sync failed. Total subreddits retrieved %d, only %d were stored", subredditsValues.length, rowsCount));
                else
                    Logger.i(String.format("%d subreddit updated", rowsCount));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Logger.e(e.getCause().getLocalizedMessage(), e.getCause());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if ( cursor != null )
                cursor.close();
        }
    }

    private ContentValues[] loadSubreddits(JSONObject result) throws JSONException {
        JSONArray subreddits = result.getJSONObject("data").getJSONArray("children");
        ContentValues[] values = new ContentValues[subreddits.length()];
        for (int i = 0; i < subreddits.length(); i++){
            values[i] = new ContentValues();
            JSONObject subreddit = subreddits.getJSONObject(i).getJSONObject("data");
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT_HTML, subreddit.optString("submit_text_html") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_BANNED, subreddit.getBoolean("user_is_banned") );
            values[i].put(ReadditContract.Subreddit.COLUMN_ID, subreddit.getString("id") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT, subreddit.optString("submit_text") );
            values[i].put(ReadditContract.Subreddit.COLUMN_DISPLAY_NAME, subreddit.getString("display_name") );
            values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_IMG, subreddit.optString("header_img") );
            values[i].put(ReadditContract.Subreddit.COLUMN_DESCRIPTION_HTML, subreddit.optString("description_html") );
            values[i].put(ReadditContract.Subreddit.COLUMN_TITLE, subreddit.optString("title") );
            values[i].put(ReadditContract.Subreddit.COLUMN_COLLAPSE_DELETED_COMMENTS, subreddit.optBoolean("collapse_deleted_comments") );
            values[i].put(ReadditContract.Subreddit.COLUMN_OVER18, subreddit.optBoolean("over18") );
            values[i].put(ReadditContract.Subreddit.COLUMN_PUBLIC_DESCRIPTION_HTML, subreddit.optString("public_description_html") );
            values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_TITLE, subreddit.optString("header_title") );
            values[i].put(ReadditContract.Subreddit.COLUMN_DESCRIPTION, subreddit.getString("description") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_LINK_LABEL, subreddit.optString("submit_link_label") );
            values[i].put(ReadditContract.Subreddit.COLUMN_ACCOUNTS_ACTIVE, subreddit.optInt("accounts_active") );
            values[i].put(ReadditContract.Subreddit.COLUMN_PUBLIC_TRAFFIC, subreddit.optBoolean("public_traffic") );
            if ( subreddit.has("header_size") && !subreddit.isNull("header_size")){
                JSONArray sizes = subreddit.getJSONArray("header_size");
                values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_WIDTH, sizes.getInt(0) );
                values[i].put(ReadditContract.Subreddit.COLUMN_HEADER_HEIGHT, sizes.getInt(1) );
            }
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBSCRIBERS, subreddit.getInt("subscribers") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMIT_TEXT_LABEL, subreddit.optString("submit_text_label") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_MODERATOR, subreddit.optBoolean("user_is_moderator") );
            values[i].put(ReadditContract.Subreddit.COLUMN_NAME, subreddit.getString("name") );
            values[i].put(ReadditContract.Subreddit.COLUMN_CREATED, subreddit.getInt("created") );
            values[i].put(ReadditContract.Subreddit.COLUMN_URL, subreddit.getString("url") );
            values[i].put(ReadditContract.Subreddit.COLUMN_CREATED_UTC, subreddit.getInt("created_utc") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_CONTRIBUTOR, subreddit.getBoolean("user_is_contributor") );
            values[i].put(ReadditContract.Subreddit.COLUMN_PUBLIC_DESCRIPTION, subreddit.optString("public_description") );
            values[i].put(ReadditContract.Subreddit.COLUMN_COMMENT_SCORE_HIDE_MINS, subreddit.getInt("comment_score_hide_mins") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBREDDIT_TYPE, subreddit.getString("subreddit_type") );
            values[i].put(ReadditContract.Subreddit.COLUMN_SUBMISSION_TYPE, subreddit.getString("submission_type") );
            values[i].put(ReadditContract.Subreddit.COLUMN_USER_IS_SUBSCRIBER, subreddit.getBoolean("user_is_subscriber") );
        }
        return values;
    }

    /**
     * Sync info about users
     * @param provider provider to access database
     */
    private void syncUser(ContentProviderClient provider){
        Logger.w("### syncUser ###");
        Cursor cursor = null;
        try {
            cursor = provider.query(ReadditContract.User.CONTENT_URI, new String[] {
                    ReadditContract.User._ID, ReadditContract.User.COLUMN_NAME, ReadditContract.User.COLUMN_ACCESSTOKEN } , null, null, null);
            while (cursor.move(1)){
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                Logger.d("updating " + name);
                String accessToken = cursor.getString(2);
                ContentValues content = new ContentValues(1);
                content.put(ReadditContract.User.COLUMN_SYNC_STATUS, SYNC_STATUS_RUNNING);
                provider.update(ReadditContract.User.CONTENT_URI, content, ReadditContract.User._ID + "=?", new String[]{String.valueOf(id)});
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                AboutRequest request = AboutRequest.newInstance(name, future, future, accessToken);
                mVolleyQueue.add(request);
                JSONObject response = future.get();
                content = new ContentValues(15);
                Logger.d(response.toString());
                response = response.getJSONObject("data");
                content.put(ReadditContract.User.COLUMN_IS_FRIEND, response.optBoolean("is_friend"));
                if ( !response.isNull("gold_expiration"))
                    content.put(ReadditContract.User.COLUMN_GOLD_EXPIRATION, response.optInt("gold_expiration"));
                content.put(ReadditContract.User.COLUMN_MODHASH, response.optString("modhash"));
                content.put(ReadditContract.User.COLUMN_HAS_VERIFIED_EMAIL, response.optBoolean("has_verified_email"));
                content.put(ReadditContract.User.COLUMN_CREATED_UTC, response.optInt("created_utc"));
                content.put(ReadditContract.User.COLUMN_ID, response.optString("id"));
                content.put(ReadditContract.User.COLUMN_HIDE_FROM_ROBOTS, response.optBoolean("hide_from_robots"));
                content.put(ReadditContract.User.COLUMN_COMMENT_KARMA, response.optInt("comment_karma"));
                content.put(ReadditContract.User.COLUMN_OVER_18, response.optBoolean("over_18"));
                content.put(ReadditContract.User.COLUMN_GOLD_CREDDITS, response.optInt("gold_creddits"));
                content.put(ReadditContract.User.COLUMN_CREATED, response.optInt("created"));
                content.put(ReadditContract.User.COLUMN_IS_GOLD, response.optBoolean("is_gold"));
                content.put(ReadditContract.User.COLUMN_NAME, response.optString("name"));
                content.put(ReadditContract.User.COLUMN_IS_MOD, response.optBoolean("is_mod"));
                content.put(ReadditContract.User.COLUMN_LINK_KARMA, response.optInt("link_karma"));
                content.put(ReadditContract.User.COLUMN_HAS_MAIL, response.optInt("has_mail"));
                content.put(ReadditContract.User.COLUMN_HAS_MOD_MAIL, response.optInt("has_mod_mail"));
                content.put(ReadditContract.User.COLUMN_SYNC_STATUS, SYNC_STATUS_NONE);
                provider.update(ReadditContract.User.CONTENT_URI, content,ReadditContract.User._ID + "=?", new String[]{String.valueOf(id)} );
                Logger.d("User " + name + " updated");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.e(e.getLocalizedMessage(), e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            VolleyError volleyError = (VolleyError) e.getCause();
            Logger.e(new String(volleyError.getNetworkResponse().data), e.getCause());
        } catch (RemoteException e) {
            e.printStackTrace();
            Logger.e(e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if ( cursor != null)
                cursor.close();
        }
    }

    public static void syncNow(Context context){
        Bundle bundle = new Bundle(1);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        context.getContentResolver().requestSync(SyncAdapter.getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        }
        return newAccount;
    }
}
