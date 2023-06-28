package com.backtocoding.stockmarket.domain.repository

import com.backtocoding.stockmarket.domain.model.CompanyListing
import com.backtocoding.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}