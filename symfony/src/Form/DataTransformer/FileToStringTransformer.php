<?php
namespace App\Form\DataTransformer;

use Symfony\Component\Form\DataTransformerInterface;
use Symfony\Component\HttpFoundation\File\File;
use Symfony\Component\HttpFoundation\File\UploadedFile;

class FileToStringTransformer implements DataTransformerInterface
{
    public function transform($file)
    {
        if (!$file instanceof File) {
            return null;
        }
        return $file->getPathname();
    }

    public function reverseTransform($file)
    {
        if (!$file) {
            return null;
        }

        if ($file instanceof UploadedFile) {
            return $file; 
        }

        return new File($file);
    }
}
