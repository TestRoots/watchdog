# Data Analysis

The data analysis methods are based on a Jupyter notebook which includes Python scripts to process the data.

To install the dependencies, run (this assumes `pip` installed on your machine):

```bash
make
```

## Fetching the data

The analysis scripts require two data files: `users.bson` and `events.bson`.
You should fetch these files from the server.
Ask the administrator for access to these files.

## Running the analyses

You can then run the notebook with the following command:
```bash
jupyter notebook
```
This should open a notebook server in your preferred browser.
Open the `data-analysis.ipynb` notebook and click `Cell -> Run All` to run all scripts.
