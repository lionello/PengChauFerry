package com.lunesu.pengchauferry

interface Fetcher<T> {
    suspend fun fetch(): List<T>
}
