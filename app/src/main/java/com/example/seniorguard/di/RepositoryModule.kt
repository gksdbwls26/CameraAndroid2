package com.example.seniorguard.di

import com.example.seniorguard.data.repository.FakeGuardianRepository
import com.example.seniorguard.data.repository.GuardianRepository
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGuardianRepository(
        impl: FakeGuardianRepository//DB연결시 수정
    ): GuardianRepository
}
