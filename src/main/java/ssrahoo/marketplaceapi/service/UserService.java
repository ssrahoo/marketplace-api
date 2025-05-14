package ssrahoo.marketplaceapi.service;

import org.springframework.stereotype.Service;
import ssrahoo.marketplaceapi.dto.UserRegistrationDto;
import ssrahoo.marketplaceapi.dto.UserUpdateDto;
import ssrahoo.marketplaceapi.entity.Buyer;
import ssrahoo.marketplaceapi.entity.Seller;
import ssrahoo.marketplaceapi.entity.User;
import ssrahoo.marketplaceapi.repository.BuyerRepository;
import ssrahoo.marketplaceapi.repository.SellerRepository;
import ssrahoo.marketplaceapi.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;
    private BuyerRepository buyerRepository;
    private SellerRepository sellerRepository;

    //dependency injection
    public UserService(UserRepository userRepository, BuyerRepository buyerRepository, SellerRepository sellerRepository) {
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
    }

    // create
    public UUID save(UserRegistrationDto userRegistrationDto){
        var user = new User(
            userRegistrationDto.username(),
            userRegistrationDto.email(),
            userRegistrationDto.password(),
            userRegistrationDto.wallet(),
            Instant.now(),
            null
        );
        var id = userRepository.save(user).getUserId();

        user.setUserId(id);

        buyerRepository.save(new Buyer(user));
        sellerRepository.save(new Seller(user));

        return id;
    }

    // read all
    public List<User> findAll(){
        return userRepository.findAll();
    }

    // read by id
    public Optional<User> findById(UUID id){
        return userRepository.findById(id);
    }

    // update
    public void updateById(UUID id, UserUpdateDto userUpdateDto){
        var result = userRepository.findById(id);

        if (result.isPresent()) {
            var user = result.get();

            if(userUpdateDto.username() != null){
                user.setUsername(userUpdateDto.username());
            }

            if(userUpdateDto.password() != null){
                user.setPassword(userUpdateDto.password());
            }

            if(userUpdateDto.wallet() != null){
                user.setWallet(userUpdateDto.wallet());
            }

            user.setModified(Instant.now());
            userRepository.save(user); // references to user in buyerRepository and sellerRepository are also updated
        }
    }

    // delete
    public void deleteById(UUID id){
        if(userRepository.existsById(id)){
            buyerRepository.deleteById(id);
            sellerRepository.deleteById(id);
            userRepository.deleteById(id);
        }
    }

}
