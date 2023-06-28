package com.backtocoding.stockmarket.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    )

    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListings()

    /**
     *  In SQLite || means we concatenate strings, similar to what we have + in Kotlin
     *  What we are doing here is we construct a string,
     *  LOWER(name) -> we convert the name to lower case
     *  LOWER(:query) -> we convert query to lower case
     *  Then we have % symbols in between so for
     *  If we search for tes then 'tes' is our query, and we check using LIKE operator
     *  if the name contains %tes% and then if that fits then it's a match
     *  OR
     *  if we convert the query to uppercase and
     *  that's equal to the symbol of the company then also it's a match
     */

    @Query(
        """
            SELECT * 
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
            UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>
}