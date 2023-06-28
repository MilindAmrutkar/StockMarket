package com.backtocoding.stockmarket.data.repository

import com.backtocoding.stockmarket.data.local.StockDatabase
import com.backtocoding.stockmarket.data.mapper.toCompanyListing
import com.backtocoding.stockmarket.data.remote.StockApi
import com.backtocoding.stockmarket.domain.model.CompanyListing
import com.backtocoding.stockmarket.domain.repository.StockRepository
import com.backtocoding.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                /**
                 *  If we do val csvReader = CSVReader(Input.......)
                 *
                 *  This violates Single Responsibility Principle
                 *  getCompanyListings() should have the responsibility to just cache data
                 *  it should not really have the responsibility and the task to
                 *  parse csv related bcoz that's not what we expect
                 *  when you read that functin name
                 */
            } catch (e: IOException) {
                // Happens when something with parsing goes wrong
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                // happens when there is an invalid response
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }
    }
}