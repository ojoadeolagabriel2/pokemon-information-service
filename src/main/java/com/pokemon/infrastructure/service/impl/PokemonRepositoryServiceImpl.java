package com.pokemon.infrastructure.service.impl;

import com.pokemon.infrastructure.client.PokemonClient;
import com.pokemon.infrastructure.client.TextTranslatorClient;
import com.pokemon.infrastructure.data.PokemonEntity;
import com.pokemon.infrastructure.service.PokemonRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.of;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokemonRepositoryServiceImpl implements PokemonRepositoryService {

    private final PokemonClient pokemonClient;
    private final TextTranslatorClient textTranslatorClient;

    @Override
    public Optional<PokemonEntity> getPokemonByName(String name) {
        var possiblePokemon = pokemonClient.getPokemonByName(name);

        if (possiblePokemon.isPresent()) {
            var pokemon = possiblePokemon.get();
            var possibleTranslation = textTranslatorClient.translate(pokemon.getDescription());

            if (possibleTranslation.isPresent()) {
                log.info("no pokemon found by this name: {}", name);
                var translation = possibleTranslation.get();

                if (translation.isSuccess()) {
                    log.info("translated {} successfully to {}", pokemon.getDescription(), translation.getContents().getTranslated());
                    return of(PokemonEntity
                            .builder()
                            .name(pokemon.getName())
                            .description(translation.getContents().getTranslated())
                            .build());
                }
            }

            log.info("no translation found for : {}", pokemon.getDescription());
            return Optional.empty();
        }

        log.info("no pokemon found by this name: {}", name);
        return Optional.empty();
    }
}