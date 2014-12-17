package com.vanzstuff.readdit.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * App contract
 * Created by vanz on 16/11/14.
 */
public class ReadditContract {

    public static final String CONTENT_AUTHORITY = "com.vanzstuff.readdit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TAG = "tag";
    public static final String PATH_POST = "post";
    public static final String PATH_POST_BY_TAG = "post_tag" ;
    public static final String PATH_COMMENT = "comment";
    public static final String PATH_SUBREDDIT = "subreddit";
    public static final String MULTIPLE_ITEM_MIMETYPE = "vnd.android.cursor.dir/";


    /**
     * table stores all tags created by the user
     */
    public static final class Tag implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_TAG;

        public static Uri buildTagUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "tag";
        public static final String COLUMN_NAME = "name";
    }

    /**
     * table stores all post tagged/saved by the user
     */
    public static final class Post implements BaseColumns{

        /** Uri used to retrieve all post */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POST).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE +  CONTENT_AUTHORITY + "/" + PATH_POST;
        public static final String CONTENT_TYPE_POST_BY_TAG = MULTIPLE_ITEM_MIMETYPE +  CONTENT_AUTHORITY + "/" + PATH_POST_BY_TAG;

        /**
         * Build a Uri with the last segment of the path is the post ID
         * @param id
         * @return uri with the post ID
         */
        public static Uri buildPostUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Build a uri to retrieve the post from a given tag
         * @param tag used to retrieve the posts
         * @return Uri to retrieve the posts
         */
        public static Uri buildPostByTagUri(String tag){
            return CONTENT_URI.buildUpon().appendPath(PATH_POST_BY_TAG).appendPath(tag).build();
        }

        /**
         * Get the tag from Uri created by the method buildPostByTagUri
         * @param uri that holds the tag
         * @return tag retrieved
         */
        static String getTagFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static final String TABLE_NAME = "post";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SUBREDDIT = "subreddit";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_VOTES = "votes";
        public static final String COLUMN_THREADS = "threads";
        public static final String COLUMN_DATE = "date";
    }

    public static final class TagXPost implements BaseColumns{
        public static final String TABLE_NAME = "tag_x_post";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_POST = "post";
    }

    /**
     * table stores all comments ( and its parents ) saved by the user
     */
    public static final class Comment implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENT).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_COMMENT;

        public static Uri buildCommentUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "comment";
        public static final String COLUMN_PARENT = "parent";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_POST = "post";
        public static final String COLUMN_DATE = "date";
    }

    /**
     * table stores all user subscribes
     */
    public static final class Subreddit implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBREDDIT).build();
        public static final String CONTENT_TYPE = MULTIPLE_ITEM_MIMETYPE + CONTENT_AUTHORITY + "/" + PATH_SUBREDDIT;

        public static Uri buildSubscribeUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "subreddit";
        public static final String COLUMN_NAME = "subreddit";
    }
}
