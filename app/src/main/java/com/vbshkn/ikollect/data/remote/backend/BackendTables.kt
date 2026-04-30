package com.vbshkn.ikollect.data.remote.backend

interface BackendTables {
    object GLOBAL {
        val ARTISTS = "global_artists"
        val ARTIST_HIERARCHY = "global_artist_hierarchy"
    }

    object USER {
        val ALBUMS = "user_albums"
        val ARTIST_SETTINGS = "user_artist_settings"
        val PHOTOCARDS = "user_photocards"
    }

    object CROSSREF {
        val ALBUM_ARTIST = "album_artist_cross_ref"
        val PHOTOCARD_ARTIST = "photocard_artist_cross_ref"
        val PHOTOCARD_TAG = "photocard_tag_cross_ref"
    }
    companion object {
        val PROFILES = "profiles"
        val TAGS = "tags"
    }
}