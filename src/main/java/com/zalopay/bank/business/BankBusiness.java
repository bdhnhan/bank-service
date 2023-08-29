package com.zalopay.bank.business;

import com.bank.protobuf.Bank;
import com.zalopay.bank.entity.BankAccount;
import com.zalopay.bank.entity.BankTransaction;
import com.zalopay.bank.enums.TransactionStatusEnum;
import com.zalopay.bank.enums.TransactionType;
import com.zalopay.bank.repository.BankAccRepo;
import com.zalopay.bank.repository.BankTransRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BankBusiness {
    private final BankAccRepo bankAccRepo;
    private final BankTransRepo bankTransRepo;

    @Value("${external.transfer-service.host}")
    private String tranferHost;
    @Value("${external.transfer-service.port}")
    private String transferPort;

    public BankBusiness(BankAccRepo bankAccRepo, BankTransRepo bankTransRepo) {
        this.bankAccRepo = bankAccRepo;
        this.bankTransRepo = bankTransRepo;
    }

    public Bank.TopUpBankResponse topUpBank(Bank.TopUpBankRequest request) {
        Bank.TopUpBankResponse.Result.Builder resultBuilder = Bank.TopUpBankResponse.Result.newBuilder();
        UUID uuid = UUID.randomUUID();
        resultBuilder.setTransId(uuid.toString());
        resultBuilder.setStatus(TransactionStatusEnum.PROCESSING.name());

        BankTransaction bankTrans = new BankTransaction();
        bankTrans.setId(uuid.toString());
        bankTrans.setNumAcc(request.getNumberAcc());
        bankTrans.setStatus(TransactionStatusEnum.PROCESSING);
        bankTrans.setAmount(request.getAmount());
        bankTrans.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        bankTrans.setTransType(TransactionType.TOP_UP);
        bankTransRepo.save(bankTrans);

        callBackTopUpTransId(request, uuid.toString());
        return Bank.TopUpBankResponse.newBuilder()
                .setResult(resultBuilder)
                .setStatus(200)
                .build();
    }

    public void callBackTopUpTransId(Bank.TopUpBankRequest request, String transId) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Long amount = request.getAmount();
            Optional<BankAccount> bankAccOpt = bankAccRepo.findByNumberAccount(request.getNumberAcc());
            Optional<BankTransaction> bankTransOpt = bankTransRepo.findById(transId);

            String url = "http://" + tranferHost + ":" + transferPort + "/transfer/callback";
            if (bankAccOpt.isPresent()) {
                Long amountCurrent = bankAccOpt.get().getBalance();
                bankAccOpt.get().setBalance(amountCurrent + amount);
                bankAccOpt.get().setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                bankAccRepo.save(bankAccOpt.get());
                String json = "{\"transId\":\""+transId+"\",\"status\":\"COMPLETED\"}";
                callBack(url, json);

                bankTransOpt.ifPresent(trans -> {
                    trans.setStatus(TransactionStatusEnum.COMPLETED);
                    bankTransRepo.save(bankTransOpt.get());
                });
            } else {
                String json = "{\"transId\":\""+transId+"\",\"status\":\"FAILED\"}";
                callBack(url, json);
                bankTransOpt.ifPresent(trans -> {
                    trans.setStatus(TransactionStatusEnum.FAILED);
                    bankTransRepo.save(bankTransOpt.get());
                });
            }
        });
        thread.start();
    }

    public void callBack(String url, String json) {
        System.out.println(url + " :: " + json);
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.getOutputStream().write(json.getBytes());
            con.connect();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Connect successfully!");
            } else {
                System.out.println("Error: " + responseCode);
            }
            con.disconnect();
        } catch (IOException e) {
            log.error("LOST CONNECTION");
        }
    }

    public void callBackWithdrawTransId(Bank.WithdrawBankRequest request, String transId) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Long amount = request.getAmount();
            Optional<BankAccount> bankAccOpt = bankAccRepo.findByNumberAccount(request.getNumberAcc());
            Optional<BankTransaction> bankTransOpt = bankTransRepo.findById(transId);

            String url = "http://" + tranferHost + ":" + transferPort + "/transfer/callback";
            if (bankAccOpt.isPresent() && bankAccOpt.get().getBalance() - amount >= 0L) {
                Long amountCurrent = bankAccOpt.get().getBalance();
                bankAccOpt.get().setBalance(amountCurrent - amount);
                bankAccOpt.get().setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                bankAccRepo.save(bankAccOpt.get());
                String json = "{\"transId\":\""+transId+"\",\"status\":\"COMPLETED\"}";
                callBack(url, json);
                bankTransOpt.ifPresent(trans -> {
                    trans.setStatus(TransactionStatusEnum.COMPLETED);
                    bankTransRepo.save(bankTransOpt.get());
                });
            } else {
                String json = "{\"transId\":\""+transId+"\",\"status\":\"FAILED\"}";
                callBack(url, json);
                bankTransOpt.ifPresent(trans -> {
                    trans.setStatus(TransactionStatusEnum.FAILED);
                    bankTransRepo.save(bankTransOpt.get());
                });
            }
        });
        thread.start();
    }

    public Bank.WithdrawBankResponse withdrawBank(Bank.WithdrawBankRequest request) {
        Bank.WithdrawBankResponse.Result.Builder resultBuilder = Bank.WithdrawBankResponse.Result.newBuilder();
        UUID uuid = UUID.randomUUID();
        resultBuilder.setTransId(uuid.toString());
        resultBuilder.setStatus(TransactionStatusEnum.PROCESSING.name());

        BankTransaction bankTrans = new BankTransaction();
        bankTrans.setId(uuid.toString());
        bankTrans.setNumAcc(request.getNumberAcc());
        bankTrans.setStatus(TransactionStatusEnum.PROCESSING);
        bankTrans.setAmount(-request.getAmount());
        bankTrans.setCreatedTime(new Timestamp(System.currentTimeMillis()));
        bankTrans.setTransType(TransactionType.WITHDRAW);
        bankTransRepo.save(bankTrans);

        callBackWithdrawTransId(request, uuid.toString());
        return Bank.WithdrawBankResponse.newBuilder()
                .setResult(resultBuilder)
                .setStatus(200)
                .build();
    }

    public void callBackRevertTransferTransId(Bank.RevertTransferBankRequest request) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String transId = request.getTransId();
            Optional<BankTransaction> bankTransOpt = bankTransRepo.findById(request.getTransId());
            String url = "http://" + tranferHost + ":" + transferPort + "/transfer/callback";
            if (bankTransOpt.isPresent()) {
                Optional<BankAccount> bankAccOpt =
                        bankAccRepo.findByNumberAccount(bankTransOpt.get().getNumAcc());
                Long amountCurrent = bankAccOpt.get().getBalance();
                bankAccOpt.get().setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                bankAccOpt.get().setBalance(amountCurrent - bankTransOpt.get().getAmount());
                bankAccRepo.save(bankAccOpt.get());
                String json = "{\"transId\":\""+transId+"\",\"status\":\"COMPLETED\"}";
                callBack(url, json);
                bankTransOpt.ifPresent(trans -> {
                    trans.setStatus(TransactionStatusEnum.ROLLBACK);
                    bankTransRepo.save(bankTransOpt.get());
                });
            } else {
                String json = "{\"transId\":\""+transId+"\",\"status\":\"FAILED\"}";
                callBack(url, json);
            }
        });
        thread.start();
    }

    public Bank.RevertTransferBankResponse revertTransfer(Bank.RevertTransferBankRequest request) {
        Bank.RevertTransferBankResponse.Result.Builder resultBuilder = Bank.RevertTransferBankResponse.Result.newBuilder();
        log.info("Receive request revert transfer :: {}", request.getTransId());

        resultBuilder.setStatus(TransactionStatusEnum.PROCESSING.name());

        callBackRevertTransferTransId(request);

        return Bank.RevertTransferBankResponse.newBuilder()
                .setResult(resultBuilder)
                .setStatus(200)
                .build();
    }
}
