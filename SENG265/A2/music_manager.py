#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Created on Wed Feb 8 14:44:33 2023
Based on: https://www.kaggle.com/datasets/arbazmohammad/world-airports-and-airlines-datasets
Sample input: --AIRLINES="airlines.yaml" --AIRPORTS="airports.yaml" --ROUTES="routes.yaml" --QUESTION="q1" --GRAPH_TYPE="bar"
@author: rivera
@author: marissaparsons (V00964692)
"""
import pandas as pd
import sys
from typing import List


"""merge_and_reduce_csv_files
    merges all given csv files into one and reduces the information in the file
    to only store artist, song, year, popularity, dancability, and energy
            Parameters
            ----------
                inputs : List[str], required
                    The filenames of the csv files.

            Returns
            -------
                pd.core.frame.DataFrame
                    returns the merged csv files in a pandas DataFrame.
"""
def merge_and_reduce_csv_files(inputs: List[str]) -> pd.core.frame.DataFrame:
    merged_df: pd.core.frame.DataFrame
    input_dfs: List[str] = []
    #drop all unnecessary information from csv files and store dataframes in input_df list
    for file in inputs:
        in_df: pd.core.frame.DataFrame
        in_df = pd.read_csv(file)
        in_df.drop(['duration_ms', 'explicit', 'key', 'loudness', 'mode', 
                 'speechiness', 'acousticness', 'instrumentalness', 
                 'liveness', 'valence', 'tempo', 'genre'], inplace=True, axis=1)
        input_dfs.append(in_df)
    
    merged_df = pd.concat(input_dfs, axis=0)
    merged_df = merged_df.reset_index(drop=True)

    return merged_df


"""find_top
    find the top number (given argument) of most popular, danceable, or energetic
    songs in the given csv files. The top amount is given and whichever one of the 
    three search parameters we are looking at is given
            Parameters
            ----------
                output_df: pd.core.frame.DataFrame, required
                    The panda dataframe we are storing our desired output in.
                sort_by: str, required
                    The given search paramater we are narrowing our results based on.
                amount: int, required
                    The number of top results we desire to find

            Returns
            -------
                pd.core.frame.DataFrame
                    returns the merged csv files in a pandas DataFrame.
"""
def find_top(output_df: pd.core.frame.DataFrame, sort_by: str, amount: int) -> pd.core.frame.DataFrame:
    output_df: pd.core.frame.DataFrame
    #sorts by highest scores in parameter and then lexicographically by song
    output_df = output_df.sort_values(by=[sort_by, 'song'], ascending=False).head(amount)

    if sort_by == 'popularity':
        output_df.drop(['danceability', 'energy'], inplace=True, axis=1)
    elif sort_by == 'danceability':
        output_df.drop(['popularity', 'energy'], inplace=True, axis=1)
    elif sort_by == 'energy':
        output_df.drop(['popularity', 'danceability'], inplace=True, axis=1)
        
    return output_df


"""split_arguments
    Splits our given arguments into a list. Three arguments are required and the second
    must be a string representation of an integer. 
            Parameters
            ----------
                None

            Returns
            -------
                list
                    returns the given arguments as a list in the order they are given.
                    order is: search parameter (one of popularity, dancability, or energy),
                    amount of top results, names of the csv files we are working from
"""
def split_arguments() -> list:
    title: str
    parameter: str
    amount: str
    filenames: str
    title, parameter = sys.argv[1].split("=")
    title, amount = sys.argv[2].split("=")
    title, filenames = sys.argv[3].split("=")
    filenames: List[str] = filenames.split(",")
    return [parameter, int(amount), filenames]


def main():
    output_df: pd.core.frame.DataFrame
    argument_list: list
    # argument_list[0] = search parameter, argument_list[1] = top amount, argument_list[2] = array of the filenames
    argument_list = split_arguments()
    output_df = merge_and_reduce_csv_files(argument_list[2])
    output_df = find_top(output_df, argument_list[0], argument_list[1])
    output_df.to_csv('output.csv', index=False)


if __name__ == '__main__':
    main()
