from zipfile import ZipFile, Path


V = 36
# indices files to retrive
N_list = [12500]*4 + [50000]
N_list = [(int(N/50*(V-1)), int(N/50*V)) for N in N_list]
# folders to retrive files
path_to_execute = ['datasets/aclImdb/test/neg/', 
                    'datasets/aclImdb/test/pos/', 
                    'datasets/aclImdb/train/neg/', 
                    'datasets/aclImdb/train/pos/', 
                    'datasets/aclImdb/train/unsup/']
zip_name = '../datasets.zip'
dest_path = '../data/'

print(N_list)

if __name__ == '__main__':

    with ZipFile(zip_name, 'r') as zip_obj:
        for path, indices in zip(path_to_execute, N_list):
            root = Path(zip_obj, at=path)
            files_to_cp = list(root.iterdir())[0 : 10]

            for file in files_to_cp:
                curr_path = path + file.name
                with open(dest_path + file.name, 'wb') as dest_file:
                    with zip_obj.open(curr_path, 'r') as origin_file:
                        dest_file.write(origin_file.read())
        
        print(path + ' ' + 'Done')