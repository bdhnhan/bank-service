package com.zalopay.bank.service;


import com.bank.protobuf.Bank;
import com.bank.protobuf.BankServiceGrpc;
import com.zalopay.bank.business.BankBusiness;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.UUID;

@GRpcService
@Slf4j
public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private final BankBusiness bankBusiness;

    public BankService(BankBusiness bankBusiness) {
        this.bankBusiness = bankBusiness;
    }

    @Override
    public void deductMoneyBank(Bank.DeductMoneyBankRequest request, StreamObserver<Bank.DeductMoneyBankResponse> responseObserver) {
        Bank.DeductMoneyBankResponse response = bankBusiness.deductMoneyBank(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addMoneyBank(Bank.AddMoneyBankRequest request, StreamObserver<Bank.AddMoneyBankResponse> responseObserver) {
        Bank.AddMoneyBankResponse response = bankBusiness.addMoneyBank(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void revertTransferBank(Bank.RevertTransferBankRequest request, StreamObserver<Bank.RevertTransferBankResponse> responseObserver) {
        Bank.RevertTransferBankResponse response = bankBusiness.revertTransfer(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getStatusTransaction(Bank.GetStatusTransactionRequest request, StreamObserver<Bank.GetStatusTransactionResponse> responseObserver) {
        Bank.GetStatusTransactionResponse response = bankBusiness.getStatusTransaction(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
