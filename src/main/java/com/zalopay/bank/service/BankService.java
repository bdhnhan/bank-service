package com.zalopay.bank.service;


import com.bank.protobuf.Bank;
import com.bank.protobuf.BankServiceGrpc;
import com.zalopay.bank.business.BankBusiness;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
@Slf4j
public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private final BankBusiness bankBusiness;

    public BankService(BankBusiness bankBusiness) {
        this.bankBusiness = bankBusiness;
    }

    @Override
    public void withdrawBank(Bank.WithdrawBankRequest request, StreamObserver<Bank.WithdrawBankResponse> responseObserver) {
        Bank.WithdrawBankResponse response = bankBusiness.withdrawBank(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void topUpBank(Bank.TopUpBankRequest request, StreamObserver<Bank.TopUpBankResponse> responseObserver) {
        Bank.TopUpBankResponse response = bankBusiness.topUpBank(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void revertTransferBank(Bank.RevertTransferBankRequest request, StreamObserver<Bank.RevertTransferBankResponse> responseObserver) {
        Bank.RevertTransferBankResponse response = bankBusiness.revertTransfer(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
